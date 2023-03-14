import pydriller.Repository;
import pydriller.*;
import java.util.*;
import java.io.*;
import java.time.*;
import java.lang.*;

public class Test {

    public static double getReadabilityScore(String javaFile) {
        String cmd = "java -jar rsm.jar " + javaFile;
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = reader.readLine();
            while (line != null && !line.startsWith("Average LOC")) {
                line = reader.readLine();
            }
            if (line != null) {
                String[] tokens = line.split("\t");
                double score = Double.parseDouble(tokens[1]);
                return score;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1.0;
    }

    public static void main(String[] args) {
        LocalDateTime dt = LocalDateTime.of(2022, 12, 31, 23, 59, 0);
        String myUrl = "https://github.com/apache/commons-vfs";
        List<List<String>> filesList = new ArrayList<>();
        List<String> listOfJavaFiles = new ArrayList<>();
        for (Commit commit : new Repository(myUrl, dt, "master").getCommits()) {
            for (Modification file : commit.getModifications()) {
                if (file.getSourceCode() != null && file.getSourceCodeBefore() != null) {
                    List<String> rowOfCsv = new ArrayList<>();
                    // file name
                    String fileName = file.getFileName();
                    rowOfCsv.add(fileName);
                    // commit message
                    String commitMsg = commit.getMsg();
                    rowOfCsv.add(commitMsg);
                    // commit hash
                    String commitHash = commit.getHash();
                    rowOfCsv.add(commitHash);
                    // author name
                    String authorName = commit.getAuthor().getName();
                    rowOfCsv.add(authorName);
                    // committer date
                    LocalDateTime committerDt = commit.getCommitterDate();
                    rowOfCsv.add(committerDt.toString());
                    // number of changed files for each commit
                    int numChangedFiles = commit.getModifications().size();
                    rowOfCsv.add(String.valueOf(numChangedFiles));
                    // old path
                    String fileOldPath = file.getOldPath();
                    rowOfCsv.add(fileOldPath);
                    // new path
                    String fileNewPath = file.getNewPath();
                    rowOfCsv.add(fileNewPath);
                    // complexity for each file
                    int fileComplexity = file.getComplexity();
                    rowOfCsv.add(String.valueOf(fileComplexity));
                    // nloc for each file
                    int fileNloc = file.getNloc();
                    rowOfCsv.add(String.valueOf(fileNloc));
                    // after version contents of the files
                    String fileContentCurrent = file.getSourceCode();
                    try {
                        BufferedWriter javaFile = new BufferedWriter(new FileWriter("current_" + fileName + ".java"));
                        javaFile.write(fileContentCurrent);
                        javaFile.close();
                        listOfJavaFiles.add("current_" + fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // before version contents of the files
                    String fileContentBefore = file.getSourceCodeBefore();
                    try {
                        BufferedWriter javaFile = new BufferedWriter(new FileWriter("before_" + fileName + ".java"));
                        javaFile.write(fileContentBefore);
                        javaFile.close();
                        listOfJavaFiles.add("before_" + fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // readability before change
                    double val1 = getReadabilityScore("before_" + fileName + ".java");
                    // readability after
