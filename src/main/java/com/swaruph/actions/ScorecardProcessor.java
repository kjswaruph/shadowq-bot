package com.swaruph.actions;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgcodecs.Imgcodecs.IMREAD_GRAYSCALE;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.INTER_AREA;
import static org.opencv.imgproc.Imgproc.MORPH_OPEN;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;
import static org.opencv.imgproc.Imgproc.RETR_TREE;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

public class ScorecardProcessor extends ListenerAdapter {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;
        TextChannel channel = event.getGuild().getTextChannelById(1311053536351617034L);
        List<Message.Attachment> attachments = event.getMessage().getAttachments();
        for (Message.Attachment attachment : attachments) {
            String filename = attachment.getFileName();
            channel.sendMessage(filename).queue();
            if (attachment.isImage()) {
                processImage(attachment, channel);
            }
        }
    }

    public void processImage(Message.Attachment attachment, TextChannel channel) {
        Tesseract instance = new Tesseract();
        instance.setDatapath(System.getenv("TESSDATA_PREFIX"));

        CompletableFuture<File> future = attachment.getProxy().downloadToFile(new File("scorecard.png"));
        future.thenAccept(file -> {
            Mat image = Imgcodecs.imread(file.getAbsolutePath(), IMREAD_GRAYSCALE);
            image = find_tables(image);
            List<List<Mat>> cell_images_row = extractCellImagesFromTable(image);
            List<List<String>> output = new ArrayList<List<String>>();

            try {
                output = readTableRows(cell_images_row, instance);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                write_csv(output, ",");
                System.out.println("Done. Output written to scoreboard.csv");
            } catch (
                    Exception e) {
                e.printStackTrace();
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    private Mat find_tables(Mat image) {
        double BLUR_KERNEL_SIZE = 3.00;
        int STD_DEV_X_DIRECTION = 0;
        int STD_DEV_Y_DIRECTION = 0;
        int MAX_COLOR_VAL = 255;
        int BLOCK_SIZE = 15;
        int SUBTRACT_FROM_MEAN = -2;
        Mat src = image;
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(src, blurred, new Size(BLUR_KERNEL_SIZE, BLUR_KERNEL_SIZE), STD_DEV_X_DIRECTION, STD_DEV_Y_DIRECTION);
        Mat inverted = new Mat();
        Core.bitwise_not(blurred, inverted);
        Mat img_bin = new Mat();
        Imgproc.adaptiveThreshold(
                inverted,
                img_bin,
                MAX_COLOR_VAL,
                ADAPTIVE_THRESH_MEAN_C,
                THRESH_BINARY,
                BLOCK_SIZE,
                SUBTRACT_FROM_MEAN
        );
        Mat vertical = img_bin.clone();
        Mat horizontal = img_bin.clone();
        int SCALE = 5;
        int image_height = horizontal.height();
        int image_width = horizontal.width();
        Mat horizontal_kernal = Imgproc.getStructuringElement(MORPH_RECT, new Size(image_width / SCALE, 1));
        Mat horizontally_opened = new Mat();
        Imgproc.morphologyEx(img_bin, horizontally_opened, MORPH_OPEN, horizontal_kernal);
        Mat vertical_kernal = Imgproc.getStructuringElement(MORPH_RECT, new Size(1, image_height / SCALE));
        Mat vertically_opened = new Mat();
        Imgproc.morphologyEx(img_bin, vertically_opened, MORPH_OPEN, vertical_kernal);
        Mat horizontally_dilated = new Mat();
        Mat vertically_dilated = new Mat();
        Imgproc.dilate(horizontally_opened, horizontally_dilated, Imgproc.getStructuringElement(MORPH_RECT, new Size(40, 1)));
        Imgproc.dilate(vertically_opened, vertically_dilated, Imgproc.getStructuringElement(MORPH_RECT, new Size(1, 60)));
        Mat mask = new Mat();
        Core.add(horizontally_dilated, vertically_dilated, mask);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

        int MIN_TABLE_AREA = (int) 1e5;
        List<MatOfPoint> filtered_contours = contours.stream()
                                                     .filter(contour -> Imgproc.contourArea(contour) > MIN_TABLE_AREA)
                                                     .toList();

        List<Double> perimeterLengths = filtered_contours.stream()
                                                         .map(contour -> Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true))
                                                         .toList();

        List<Double> epsilons = perimeterLengths.stream()
                                                .map(p -> 0.1 * p)
                                                .toList();

        List<MatOfPoint> approx_polys = IntStream.range(0, filtered_contours.size())
                                                 .mapToObj(i -> {
                                                     MatOfPoint contour = filtered_contours.get(i);
                                                     double epsilon = epsilons.get(i);
                                                     MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
                                                     MatOfPoint2f approxpolyf = new MatOfPoint2f();
                                                     Imgproc.approxPolyDP(contour2f, approxpolyf, epsilon, true);
                                                     return new MatOfPoint(approxpolyf.toArray());
                                                 })
                                                 .toList();

        List<Rect> bounding_rects = approx_polys.stream()
                                                .map(Imgproc::boundingRect)
                                                .toList();

        List<Mat> images = bounding_rects.stream()
                                         .map(rect -> new Mat(src, rect))
                                         .toList();
        System.out.println(images.get(0).size());
        return images.get(0);
    }

    private static List<List<Mat>> extractCellImagesFromTable(Mat image) {
        // Constants
        final Size BLUR_KERNEL_SIZE = new Size(1, 1);
        final int MAX_COLOR_VAL = 255;
        final int BLOCK_SIZE = 13;
        final int SUBTRACT_FROM_MEAN = -1;
        final int SCALE = 9;
        final int MIN_RECT_WIDTH = 40;
        final int MIN_RECT_HEIGHT = 10;

        Mat blurred = new Mat();
        Imgproc.GaussianBlur(image, blurred, BLUR_KERNEL_SIZE, 0);

        Mat inverted = new Mat();
        Core.bitwise_not(blurred, inverted);
        Mat imgBin = new Mat();
        Imgproc.adaptiveThreshold(
                inverted,
                imgBin,
                MAX_COLOR_VAL,
                Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY,
                BLOCK_SIZE,
                SUBTRACT_FROM_MEAN
        );

        int imageWidth = imgBin.width();
        int imageHeight = imgBin.height();

        Mat horizontalKernel = Imgproc.getStructuringElement(
                Imgproc.MORPH_RECT,
                new Size((double) imageWidth / SCALE, 1)
        );

        Mat verticalKernel = Imgproc.getStructuringElement(
                Imgproc.MORPH_RECT,
                new Size(1, (double) imageHeight / SCALE)
        );

        Mat horizontallyOpened = new Mat();
        Mat verticallyOpened = new Mat();

        Imgproc.morphologyEx(imgBin, horizontallyOpened, Imgproc.MORPH_OPEN, horizontalKernel);
        Imgproc.morphologyEx(imgBin, verticallyOpened, Imgproc.MORPH_OPEN, verticalKernel);

        Mat horizontallyDilated = new Mat();
        Mat verticallyDilated = new Mat();

        Mat horizontalDilationKernel = Imgproc.getStructuringElement(
                Imgproc.MORPH_RECT,
                new Size(1038, 1)
        );

        Mat verticalDilationKernel = Imgproc.getStructuringElement(
                Imgproc.MORPH_RECT,
                new Size(1, 60)
        );

        Imgproc.dilate(horizontallyOpened, horizontallyDilated, horizontalDilationKernel);
        Imgproc.dilate(verticallyOpened, verticallyDilated, verticalDilationKernel);

        Mat vertically_eroded = new Mat();
        Mat erosionElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.erode(verticallyDilated, vertically_eroded, erosionElement);

        Mat mask = new Mat();
        Core.add(horizontallyDilated, vertically_eroded, mask);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(
                mask,
                contours,
                hierarchy,
                Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE
        );

        List<MatOfPoint> approxPolys = contours.stream().map(matOfPoint -> {
                                                   double epsilon = 0.02 * Imgproc.arcLength(new MatOfPoint2f(matOfPoint.toArray()), true); // Replace with appropriate epsilon
                                                   MatOfPoint2f contour2f = new MatOfPoint2f(matOfPoint.toArray());
                                                   MatOfPoint2f approxpoly2f = new MatOfPoint2f();
                                                   Imgproc.approxPolyDP(contour2f, approxpoly2f, epsilon, true);
                                                   return new MatOfPoint(approxpoly2f.toArray());
                                               })
                                               .toList();

        List<Rect> boundingRects = new ArrayList<>();
        for (MatOfPoint poly : approxPolys) {
            Rect rect = Imgproc.boundingRect(poly);
            boundingRects.add(rect);
        }

        Mat imageCopy = image.clone();
        for (Rect rect : boundingRects) {
            Imgproc.rectangle(
                    imageCopy,
                    new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0),
                    2
            );
        }

        boundingRects = boundingRects.stream()
                                     .filter(r -> r.width > MIN_RECT_WIDTH && r.height > MIN_RECT_HEIGHT)
                                     .collect(Collectors.toList());

        Rect largestRect = boundingRects.stream()
                                        .max(Comparator.comparingInt(r -> r.width * r.height))
                                        .orElse(null);

        if (largestRect != null) {
            boundingRects.remove(largestRect);
        }

        List<List<Rect>> rows = groupCellsIntoRows(boundingRects);
        List<List<Mat>> cellImagesRows = new ArrayList<>();
        for (List<Rect> row : rows) {
            List<Mat> cellImagesRow = new ArrayList<>();
            for (Rect rect : row) {
                int offset = 56;
                Rect adjustedRect = new Rect(
                        rect.x + offset,
                        rect.y,
                        rect.width - offset,
                        rect.height
                );
                Mat cellImage = new Mat(image, adjustedRect);
                cellImagesRow.add(cellImage);
            }
            cellImagesRows.add(cellImagesRow);
        }
        cellImagesRows.remove(0);
        return cellImagesRows;
    }

    private static List<List<Rect>> groupCellsIntoRows(List<Rect> cells) {
        List<List<Rect>> rows = new ArrayList<>();
        List<Rect> remainingCells = new ArrayList<>(cells);

        while (!remainingCells.isEmpty()) {
            Rect firstCell = remainingCells.get(0);
            List<Rect> rowCells = new ArrayList<>();
            rowCells.add(firstCell);

            List<Rect> cellsToRemove = new ArrayList<>();
            for (Rect cell : remainingCells.subList(1, remainingCells.size())) {
                if (cellInSameRow(firstCell, cell)) {
                    rowCells.add(cell);
                    cellsToRemove.add(cell);
                }
            }
            remainingCells.removeAll(cellsToRemove);
            remainingCells.remove(firstCell);

            rowCells.sort(Comparator.comparingInt(r -> r.x));
            rows.add(rowCells);
        }

        rows.sort(Comparator.comparingDouble(row ->
                row.stream()
                   .mapToDouble(r -> r.y + r.height - r.height / 2.0)
                   .average()
                   .orElse(0)
        ));
        return rows;
    }

    private static boolean cellInSameRow(Rect c1, Rect c2) {
        double c1Center = c1.y + c1.height - c1.height / 2.0;
        double c2Bottom = c2.y + c2.height;
        double c2Top = c2.y;

        return c2Top < c1Center && c1Center < c2Bottom;
    }

    private List<List<String>> readTableRows(List<List<Mat>> cellImagesRows, Tesseract instance) throws IOException {
        List<List<String>> output = new ArrayList<>();
        int n = 0;
        int SCALE = 10;
        for (List<Mat> row : cellImagesRows) {
            List<String> tempOutput = new ArrayList<>();
            n++;
//            Imgcodecs.imwrite("test_rows" + n + ".png", row.get(0));
            Mat image = row.get(0);

            List<Rect> cells = rowSeperator(image, new Size(9, 9));
            cells.sort(Comparator.comparingInt(c -> c.x));

            Mat finalImage = image.clone();

            cells.removeIf(c -> c.x <= (0.24 * finalImage.width()));

            if (cells.size() != 8) {
                cells = rowSeperator(image, new Size(11, 11));
                cells.sort(Comparator.comparingInt(c -> c.x));
                cells.removeIf(c -> c.x <= (0.24 * finalImage.width()));
            }

//            Mat imageCopy = finalImage.clone();
//            for (Rect cnt : cells) {
//                Imgproc.rectangle(imageCopy, new Point(cnt.x, cnt.y), new Point(cnt.x + cnt.width, cnt.y + cnt.height), new Scalar(0, 0, 255), 2);
//            }
//            Imgcodecs.imwrite("yo.png", imageCopy);

            image = imageProcess(finalImage);

            int startRow = 0;
            int endRow = Math.min(100 * SCALE, image.rows()); // Ensure endRow does not exceed image height
            int startCol = 0;
            int endCol = Math.min(300 * SCALE, image.cols());
            Mat name = image.submat(startRow, endRow, startCol, endCol);
//            Imgcodecs.imwrite("name.png", name);

            String ocrName = ocrImage(name, instance, "alphabets");
            tempOutput.add(ocrName.strip());

            for (int c = 0; c < cells.size(); c++) {
                Rect cell = cells.get(c);
                Mat cropped = image.submat(new Rect(cell.x * SCALE, cell.y * SCALE, cell.width * SCALE, cell.height * SCALE));
                cropped = imageResize(cropped, 20);

                Mat kernel = Mat.ones(new Size(1, 1), CvType.CV_8U);
                Mat dilated = new Mat();
                Imgproc.dilate(cropped, dilated, kernel);

//                Mat bordered = new Mat();
//                Core.copyMakeBorder(dilated, bordered, 2, 2, 2, 2, Core.BORDER_CONSTANT);
//                Imgcodecs.imwrite("crop" + c + ".png", dilated);

                String ocrCropped = ocrImage(dilated, instance, "numbers");
                System.out.println(ocrCropped);
                tempOutput.add(ocrCropped.strip());
            }
            tempOutput.removeIf(String::isEmpty);
            output.add(tempOutput);
        }
        output.sort(Comparator.comparing(row -> row.get(0)));
        return output;
    }

    private Mat imageResize(Mat image, int scalePercent) {
        int width =  image.width() * scalePercent / 100;
        int height = image.height() * scalePercent / 100;

        Size size = new Size(width, height);

        Mat resizedImage = new Mat();
        Imgproc.resize(image, resizedImage, size, INTER_AREA);
        return resizedImage;
    }

    private List<Rect> getNonOverlappingRectangles(List<Rect> rectangles) {
        List<Rect> nonOverlappingRectangles = new ArrayList<>();
        Set<Rect> overlappingRectangles = new HashSet<>();

        for (int i = 0; i < rectangles.size(); i++) {
            Rect rect1 = rectangles.get(i);
            boolean overlaps = false;
            Rect largerRect = rect1;
            Rect smallerRect = null;

            for (int j = 0; j < rectangles.size(); j++) {
                if (i != j) {
                    Rect rect2 = rectangles.get(j);
                    if (overlaps(rect1, rect2)) {
                        overlaps = true;
                        if (area(rect1) > area(rect2)) {
                            largerRect = rect1;
                            smallerRect = rect2;
                        } else {
                            largerRect = rect2;
                            smallerRect = rect1;
                        }
                        break;
                    }
                }
            }
            if (overlaps) {
                if (smallerRect != null) {
                    overlappingRectangles.add(smallerRect);
                }
            } else {
                nonOverlappingRectangles.add(rect1);
            }
            nonOverlappingRectangles.add(largerRect);
        }

        nonOverlappingRectangles.removeAll(overlappingRectangles);

        Set<Rect> uniqueRectangles = new HashSet<>(nonOverlappingRectangles);
        nonOverlappingRectangles.clear();
        nonOverlappingRectangles.addAll(uniqueRectangles);

        return nonOverlappingRectangles;
    }

    private static boolean overlaps(Rect rect1, Rect rect2) {
        return rect1.x < rect2.x + rect2.width && rect1.x + rect1.width > rect2.x
                && rect1.y < rect2.y + rect2.height && rect1.y + rect1.height > rect2.y;
    }

    private static int area(Rect rect) {
        return rect.width * rect.height;
    }

    private Mat imageProcess(Mat image) {
        int scalePercent = 1000;
        int width = image.width() * scalePercent / 100;
        int height = image.height() * scalePercent / 100;
        Size size = new Size(width, height);

        Mat resizeImage = new Mat();
        Imgproc.resize(image, resizeImage, size, INTER_AREA);

        Mat kernel = Mat.ones(new Size(5, 5), CvType.CV_8U);

        Mat dilated = new Mat();
        Imgproc.dilate(resizeImage, dilated, kernel);

        Mat blurred = new Mat();
        Imgproc.GaussianBlur(dilated, blurred, new Size(3, 3), 0);

        Mat threshold = new Mat();
        Imgproc.threshold(blurred, threshold, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

        Mat noNoise = new Mat();
        Imgproc.medianBlur(threshold, noNoise, 9);

        Mat invertedImage = new Mat();
        Core.bitwise_not(noNoise, invertedImage);

        return invertedImage;
    }

    private String ocrImage(Mat image, Tesseract instance, String config) throws IOException {
        if(config=="numbers") {
            instance.setVariable("tessedit_char_whitelist", "0123456789");
            instance.setVariable("tessedit_pageseg_mode", "7"); // Use '7' for single line mode
        }else {
            instance.setVariable("tessedit_char_whitelist", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/");
            instance.setVariable("tessedit_pageseg_mode", "7"); // Use '7' for single line mode
        }
        BufferedImage bufferedImage = matToBufferedImage(image);
        try {
            return instance.doOCR(bufferedImage);
        } catch (
                TesseractException e) {
            e.printStackTrace();
            return "";
        }
    }

    private BufferedImage matToBufferedImage(Mat mat) throws IOException {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", mat, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        return ImageIO.read(inputStream);
    }

    private List<Rect> rowSeperator(Mat image, Size size) {
        Size BLUR_KERNAL_SIZE = size;
        int STD_DEV_X_DIRECTION = 0;
        int STD_DEV_Y_DIRECTION = 0;
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(image, blurred, BLUR_KERNAL_SIZE, STD_DEV_X_DIRECTION, STD_DEV_Y_DIRECTION);
        int MAX_COLOR_VAL = 255;
        int BLOCK_SIZE = 11;
        int SUBTRACT_FROM_MEAN = -1;
        Mat inverted = new Mat();
        Core.bitwise_not(blurred, inverted);
        Mat img_bin = new Mat();
        Imgproc.adaptiveThreshold(inverted, img_bin, MAX_COLOR_VAL, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, BLOCK_SIZE, SUBTRACT_FROM_MEAN);
        Mat horizontal = img_bin.clone();
        Mat vertical = img_bin.clone();
        int SCALE = 20;
        int imageWidth = horizontal.width();
        int imageHeight = vertical.height();
        Mat horizontalKernel = Imgproc.getStructuringElement(MORPH_RECT, new Size(imageWidth / SCALE, 1));
        Mat verticalKernel = Imgproc.getStructuringElement(MORPH_RECT, new Size(1, imageHeight / SCALE));
        Mat horizontallyOpened = new Mat();
        Mat verticallyOpened = new Mat();
        Imgproc.morphologyEx(img_bin, horizontallyOpened, MORPH_OPEN, horizontalKernel);
        Imgproc.morphologyEx(img_bin, verticallyOpened, MORPH_OPEN, verticalKernel);

        Mat horizontallyDilated = new Mat();
        Mat verticallyDilated = new Mat();
        Imgproc.dilate(horizontallyOpened, horizontallyDilated, Imgproc.getStructuringElement(MORPH_RECT, new Size(1038, 1)));
        Imgproc.dilate(verticallyOpened, verticallyDilated, Imgproc.getStructuringElement(MORPH_RECT, new Size(1, 60)));

        Mat mask = new Mat();
        Core.add(horizontallyDilated, verticallyDilated, mask);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(img_bin, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE);

        List<Double> perimeterLenghts = contours.stream()
                                                .map(p -> Imgproc.arcLength(new MatOfPoint2f(p.toArray()), true))
                                                .toList();
        List<Double> epsilons = perimeterLenghts.stream()
                                                .map(e -> 0.05 * e)
                                                .toList();
        List<MatOfPoint> approx_polys = IntStream.range(0, contours.size())
                                                 .mapToObj(i -> {
                                                     MatOfPoint contour = contours.get(i);
                                                     double epsilon = epsilons.get(i);
                                                     MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
                                                     MatOfPoint2f approxpolyf = new MatOfPoint2f();
                                                     Imgproc.approxPolyDP(contour2f, approxpolyf, epsilon, true);
                                                     return new MatOfPoint(approxpolyf.toArray());
                                                 })
                                                 .toList();
        List<MatOfPoint> approx_rects = approx_polys.stream()
                                                    .filter(p -> p.total() == 4)
                                                    .toList();
        List<Rect> bounding_rects = approx_polys.stream()
                                                .map(Imgproc::boundingRect)
                                                .toList();
        int MIN_RECT_WIDTH = 9;
        int MIN_RECT_HEIGHT = 14;
        List<Rect> filteredBoundingRects = bounding_rects.stream()
                                                         .filter(r -> r.width > MIN_RECT_WIDTH && r.height > MIN_RECT_HEIGHT)
                                                         .toList();

        Optional<Rect> largest_rect = filteredBoundingRects.stream()
                                                           .max(Comparator.comparingInt(r -> r.width * r.height));
        List<Rect> filteredBoundingRectswithoutLargest = largest_rect
                .map(l -> filteredBoundingRects.stream()
                                               .filter(rect -> !rect.equals(largest_rect))
                                               .toList())
                .orElse(filteredBoundingRects);

        filteredBoundingRectswithoutLargest = getNonOverlappingRectangles(filteredBoundingRectswithoutLargest);

        List<Rect> cells = new ArrayList<>(filteredBoundingRectswithoutLargest);
        return cells;
    }

    private void write_csv(List<List<String>> output, String delimiter) {
        CSVFormat format = CSVFormat.DEFAULT.withDelimiter(delimiter.charAt(0));
        Path path = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "scorecard.csv");

        try (
                BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
                CSVPrinter printer = new CSVPrinter(writer, format)
        ) {
            for (List<String> record : output) {
                printer.printRecord(record);
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}

