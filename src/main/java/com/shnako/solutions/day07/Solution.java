package com.shnako.solutions.day07;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
The first step in this solution is reading the input into a tree of Folder and File objects, simulating a file system.
This will return the root folder as a Folder.

We can traverse this tree recursively to find out information from it.
For part 1, we traverse the tree counting the number of folders smaller than the specified size (countFoldersSmallerThanSize).
For part 2, we traverse the tree looking for the smallest size larger than what we need to free up (findSmallestFolderLargerThanSize).

There are optimizations that could be done to avoid recursively traversing the tree more than needed,
especially around the getFolderSize() method, but the program is very fast as it is so decided to favour readability.
 */

public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        Folder rootFolder = readInput();
        return rootFolder.countFoldersSmallerThanSize(100000) + "";
    }

    @Override
    public String runPart2() throws Exception {
        Folder rootFolder = readInput();
        int sizeToFreeUp = rootFolder.getFolderSize() - (70000000 - 30000000);
        return rootFolder.findSmallestFolderLargerThanSize(sizeToFreeUp).getFolderSize() + "";
    }

    private Folder readInput() throws IOException {
        List<String> terminal = InputProcessingUtil.readInputLines(getDay());

        Folder rootFolder = new Folder("/", null);
        Folder currentFolder = rootFolder;
        for (int i = 0; i < terminal.size(); i++) {
            switch (terminal.get(i)) {
                case "$ ls" -> {
                    while (i < terminal.size() - 1) {
                        i++;
                        if (terminal.get(i).startsWith("$")) {
                            i--;
                            break;
                        }
                        String[] components = terminal.get(i).split(" ");
                        if ("dir".equals(components[0])) {
                            currentFolder.childFolders.add(new Folder(components[1], currentFolder));
                        } else {
                            currentFolder.childFiles.add(new File(components[1], Integer.parseInt(components[0])));
                        }
                    }
                }
                case "$ cd /" -> currentFolder = rootFolder;
                case "$ cd .." -> currentFolder = currentFolder.parentFolder;
                default -> currentFolder = currentFolder.getChildFolderWithName(terminal.get(i).substring(5));
            }
        }

        return rootFolder;
    }

    private record Folder(String name, Folder parentFolder, List<Folder> childFolders, List<File> childFiles) {
        public Folder(String folderName, Folder parentFolder) {
            this(folderName, parentFolder, new ArrayList<>(), new ArrayList<>());
        }

        @Override
        public String toString() {
            return name + " | " + getFolderSize();
        }

        public Folder getChildFolderWithName(String childFolderName) {
            return childFolders
                    .stream()
                    .filter(folder -> folder.name.equals(childFolderName))
                    .findFirst()
                    .orElseThrow();
        }

        public int getFolderSize() {
            int size = 0;
            for (Folder childFolder : childFolders) {
                size += childFolder.getFolderSize();
            }
            for (File childFile : childFiles) {
                size += childFile.fileSize;
            }
            return size;
        }

        public int countFoldersSmallerThanSize(int size) {
            int count = getFolderSize() < size ? getFolderSize() : 0;
            for (Folder folder : childFolders) {
                count += folder.countFoldersSmallerThanSize(size);
            }
            return count;
        }

        public Folder findSmallestFolderLargerThanSize(int size) {
            Folder result = getFolderSize() > size ? this : null;
            for (Folder folder : childFolders) {
                Folder candidateFolder = folder.findSmallestFolderLargerThanSize(size);
                if (candidateFolder != null && (result == null || candidateFolder.getFolderSize() < result.getFolderSize())) {
                    result = candidateFolder;
                }
            }
            return result;
        }
    }

    private record File(String name, int fileSize) {
    }
}