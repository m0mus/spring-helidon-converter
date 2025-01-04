package converter;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileLister {

    public static List<Path> listFiles(String rootDir, List<String> exclusions, List<String> inclusions) {
        var files = new ArrayList<Path>();
        var root = new File(rootDir);

        if (!root.isDirectory()) {
            throw new IllegalArgumentException("The specified path is not a directory: " + rootDir);
        }

        var exclusionPatterns = new ArrayList<Pattern>();
        for (var exclusion : exclusions) {
            exclusionPatterns.add(Pattern.compile(globToRegex(exclusion)));
        }

        var inclusionPatterns = new ArrayList<Pattern>();
        for (var inclusion : inclusions) {
            inclusionPatterns.add(Pattern.compile(globToRegex(inclusion)));
        }

        listFilesRecursive(root, root.toPath(), exclusionPatterns, inclusionPatterns, files);
        return files;
    }

    private static void listFilesRecursive(File directory, Path rootPath, List<Pattern> exclusionPatterns, List<Pattern> inclusionPatterns, List<Path> files) {
        var fileList = directory.listFiles();

        if (fileList == null) {
            return; // Handle case where directory is empty or inaccessible
        }

        for (var file : fileList) {
            var relativePath = rootPath.relativize(file.toPath());
            var relativePathString = relativePath.toString();

            if (matchesExclusions(relativePathString, exclusionPatterns)) {
                continue; // Skip excluded files or directories
            }

            if (file.isDirectory()) {
                listFilesRecursive(file, rootPath, exclusionPatterns, inclusionPatterns, files);
            } else {
                if (inclusionPatterns.isEmpty() || matchesInclusions(relativePathString, inclusionPatterns)) {
                    files.add(file.toPath());
                }
            }
        }
    }

    private static boolean matchesExclusions(String filePath, List<Pattern> exclusionPatterns) {
        for (var pattern : exclusionPatterns) {
            if (pattern.matcher(filePath).matches()) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesInclusions(String filePath, List<Pattern> inclusionPatterns) {
        for (var pattern : inclusionPatterns) {
            if (pattern.matcher(filePath).matches()) {
                return true;
            }
        }
        return false;
    }

    private static String globToRegex(String glob) {
        var regex = new StringBuilder();
        var length = glob.length();
        for (var i = 0; i < length; i++) {
            var c = glob.charAt(i);
            switch (c) {
            case '*':
                regex.append(".*");
                break;
            case '?':
                regex.append('.');
                break;
            case '.':
            case '\\':
            case '[':
            case ']':
            case '(':
            case ')':
            case '{':
            case '}':
            case '^':
            case '$':
            case '|':
            case '+':
                regex.append('\\').append(c);
                break;
            default:
                regex.append(c);
                break;
            }
        }
        return regex.toString();
    }
}
