package com.anttijuustila.tira;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class BookImplementation implements Book {

    private class Node {
        long key;
        String value;
        int counter = 0;
        Node left = null;
        Node right = null;
        Node parent = null;

        public Node(int number, String word) {
            key = number;
            value = word;
            left = null;
            right = null;
            counter = 1;
        }
    }

    int height = 0;
    int max = 0;
    int size = 0;
    Node root = null;
    Node node = null;
    Node x = null;
    Node parent = null;
    int lastIndex = 0;
    int j = 0;
    int p = 0;

    File testfile;
    File ignorefile;
    private String[] ignore = new String[50];
    int totalwordcounter = 0;
    int uniquewordcounter = 0;
    int ignoredwordscounter = 0;
    int ignorewordcount = 0;
    Node[] allNodes = new Node[size];

    @Override
    public void setSource(String fileName, String ignoreWordsFile) throws FileNotFoundException {

        // kaikki testit menevät täällä VS codessa läpi, mutta terminalissa/cmd:ssä täytyy laittaa tiedostoja kysyessä polku seuraavasti: src/test/resources/Bulk.txt tällä tavalla testit saa ajettua onnnistuneesti läpi myös muissa ympäräistöissä
        testfile = new File(fileName);
        ignorefile = new File(ignoreWordsFile);

        if (testfile.exists() && ignorefile.exists()) {

        } else {
            throw new FileNotFoundException("The file is not found!");
        }
    }

    @Override
    public void countUniqueWords() throws IOException, OutOfMemoryError {

        String line;
        int index1 = 0;
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;

        try {
            fileReader = new FileReader(ignorefile, StandardCharsets.UTF_8);
            bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                line = line.toLowerCase();
                String[] ignoreWords = line.split(",");

                for (int i = 0; i < ignoreWords.length; i++) {
                    ignore[index1] = ignoreWords[i];
                    index1++;
                    ignorewordcount++;
                }
            }

            fileReader.close();

            Reader fileReader2 = new InputStreamReader(new FileInputStream(testfile), "UTF-8");

            String word = "";
            int letter2 = 0;
            while ((letter2 = fileReader2.read()) != -1) {
                char c = (char) letter2;
                if (Character.isLetter(letter2)) {
                    word = word + c;

                } else {
                    word = word.toLowerCase();
                    validWord(word);
                    word = "";

                }
            }

            allNodes = new Node[size];
            inOrder(allNodes, root);
            quicksort(allNodes, 0, allNodes.length - 1);
            fileReader2.close();
        } catch (IOException e) {
            throw new IOException("Reading the file failed");
        }

    }

    private void validWord(String word) {

        boolean ignoreWord = false;
        int i = 0;
        while (ignore[i] != null) {
            if (ignore[i].equals(word)) {
                ignoreWord = true;
                ignoredwordscounter++;
                break;
            }
            i++;
        }

        if (!ignoreWord) {
            if (word.length() > 1) {
                totalwordcounter++;
                addToTree(word);
            }
        }

    }

    public boolean addToTree(String word) throws IllegalArgumentException {
        if (word == null) {
            throw new IllegalArgumentException("Word is null");
        }
        try {
            height = 0;
            int hash = calculateHashCode(word);
            node = root;
            while (node != null && node.key != hash) {
                parent = node;
                if (hash < node.key) {
                    node = node.left;
                } else {
                    node = node.right;
                }
                height++;
            }
            if (height > max) {
                max = height + 1;
            }

            if (node != null) {
                node.counter++;
                return false;
            } else {
                node = new Node(hash, word);
                node.parent = parent;
                if (root == null) {
                    root = node;
                } else if (node.key < parent.key) {
                    parent.left = node;
                } else {
                    parent.right = node;
                }
            }
            size++;
            uniquewordcounter++;

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Couldn't add to tree");
        }
    }

    public int calculateHashCode(String word) {
        int hash = 31;
        for (int i = 0; i < word.length(); i++) {
            hash = hash * 31 + word.charAt(i);
        }
        return hash;
    }


    @Override
    public void report() {
        int k = 1;
        if (size > 100) {
            for (int i = 0; i < 100; i++) {
                System.out.println(k + ".     " + allNodes[i].value + "     " + allNodes[i].counter);
                k++;
            }
        } else {
            for (int i = 0; i < size; i++) {
                System.out.println(k + ".     " + allNodes[i].value + "     " + allNodes[i].counter);
                k++;
            }
        }

        System.out.println("The total number of words in the file: " + getTotalWordCount());
        System.out.println("The count of unique words in the file " + getUniqueWordCount());
        System.out.println("The count of ignored words: " + ignoredwordscounter);
        System.out.println("The count of words to ignore: " + ignorewordcount);
        System.out.println("The height of the tree is: " + max);
    }

    public static void quicksort(Node[] array, int low, int high) {
        while (low < high) {
            int mid = low + (high - low) / 2;
            int i = low - 1;
            int j = high + 1;
            Node pivot = array[mid];
            Node tmp;
            while (true) {
                do{
                    i = i + 1;
                }while(array[i].counter > pivot.counter);

                do{
                    j = j - 1;
                }while(array[j].counter < pivot.counter);
    
                if (i >= j)
                    break;
                tmp = array[i];
                array[i] = array[j];
                array[j] = tmp;
            }
            i = j++;
            if ((i - low) <= (high - j)) {
                quicksort(array, low, i);
                low = j;
            } else {
                quicksort(array, j, high);
                high = i;
            }
        }
    }

    public void inOrder(Node[] array, Node node) {
        if (node != null) {
            inOrder(array, node.left);
            array[p] = node;
            p++;
            inOrder(array, node.right);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public int getUniqueWordCount() {
        return uniquewordcounter;
    }

    @Override
    public int getTotalWordCount() {
        return totalwordcounter;
    }

    @Override
    public String getWordInListAt(int position) {
        String listWord = allNodes[position].value;
        return listWord;
    }

    @Override
    public int getWordCountInListAt(int position) {
        int wordCount = allNodes[position].counter;
        return wordCount;
    }

}
