import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import gr.gousiosg.javacg.stat.JCallGraph;
import gr.gousiosg.javacg.stat.JCallGraph.ClassVisitor;

public class ReadabilityScoreAfterCommit {
    
    public static void main(String[] args) {
        String myUrl = "https://github.com/apache/commons-vfs";
        DateTime dt = new DateTime(2022, 12, 31, 23, 59, 0);
        List<String[]> filesList = new ArrayList<String[]>();
        List<String> listOfJavaFiles = new ArrayList<String>();
        for (gr.gousiosg.javacg.stat.CalledMethods cm : JCallGraph.run(myUrl, dt, "master")) {
            for (ClassVisitor cv : cm.classes.values()) {
                for (String javaFile : cv.getJavaFiles()) {
                    String rowOfCsv = "";
                    // file name
                    rowOfCsv += javaFile + ",";
                    // commit message
                    rowOfCsv += cm.commitMsg + ",";
                    // commit hash
                    rowOfCsv += cm.commitHash + ",";
                    // author name
                    rowOfCsv += cm.authorName + ",";
                    // committer date
                    rowOfCsv += cm.committerDate.toString() + ",";
                    // number of changed files for each commit
                    rowOfCsv += cm.numChangedFiles + ",";
                    // old path
                    rowOfCsv += javaFile + ",";
                    // new path
                    rowOfCsv += javaFile + ",";
                    // complexity for each file
                    int fileComplexity = cv.getComplexity(javaFile);
                    rowOfCsv += fileComplexity + ",";
                    // nloc for each file
                    int fileNloc = cv.getNloc(javaFile);
                    rowOfCsv += fileNloc + ",";
                    // after version contents of the files
                    String fileContentCurrent = cv.getSourceCode(javaFile);
                    try {
                        FileWriter javaFileWriter = new FileWriter("current_" + javaFile);
                        javaFileWriter.write(fileContentCurrent);
                        javaFileWriter.close();
                        listOfJavaFiles.add("current_" + javaFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // before version contents of the files
                    String fileContentBefore = cv.getSourceCodeBefore(javaFile);
                    try {
                        FileWriter javaFileWriter = new FileWriter("before