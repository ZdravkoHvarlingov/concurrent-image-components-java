package concurrent.algorithms;

import concurrent.algorithms.components.ComponentsDrawer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.cli.*;


public class Boot {

    private static CLIParams parseParams(String[] args) {
        Options options = new Options();

        Option imagePathOpt = new Option("i", "image", true, "input image file");
        imagePathOpt.setRequired(true);
        options.addOption(imagePathOpt);

        Option numberOfThreadsOpt = new Option("t", "threads", true, "number of threads");
        numberOfThreadsOpt.setRequired(true);
        options.addOption(numberOfThreadsOpt);

        Option similarityOpt = new Option("s", "similarity", true, "grayscale similarity");
        similarityOpt.setRequired(true);
        options.addOption(similarityOpt);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("components-finder", options);

            return null;
        }

        return new CLIParams(
                cmd.getOptionValue("image"),
                Integer.parseInt(cmd.getOptionValue("threads")),
                Integer.parseInt(cmd.getOptionValue("similarity"))
        );
    }

    public static void main(String[] args) {
        CLIParams params = parseParams(args);
        if (params == null) {
            return;
        }

        nu.pattern.OpenCV.loadLocally();
        ComponentsDrawer componentsDrawer = new ComponentsDrawer();
        componentsDrawer.drawAndSave(
                params.getInputImageFile(),
                params.getNumberOfThreads(),
                params.getSimilarity()
        );
    }

    @Getter
    @AllArgsConstructor
    private static class CLIParams {

        private String inputImageFile;
        private int numberOfThreads;
        private int similarity;
    }
}
