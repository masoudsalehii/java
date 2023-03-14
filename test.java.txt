import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReadabilityScoreBeforeCommit {
    
    public static void main(String[] args) {
        String javaFile = "test.java";
        float readabilityScore = getReadabilityScore(javaFile);
        System.out.printf("The readability score of %s is %f%n", javaFile, readabilityScore);
    }
    
    public static float getReadabilityScore(String javaFile) {
        String cmd = String.format("java -jar rsm.jar %s", javaFile);
        ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
        pb.redirectErrorStream(true);
        try {
            Process process = pb.start();
            process.waitFor(10, TimeUnit.SECONDS);
            String output = new String(process.getInputStream().readAllBytes());
            String[] outputLines = output.split("\n");
            float score = Float.parseFloat(outputLines[2].split("\t")[1]);
            return score;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
