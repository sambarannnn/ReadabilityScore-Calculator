package readability;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ReadabilityEngine {
    private String text = "";
    private int word_count = 0;
    private int char_count = 0;
    private int sentence_count = 0;
    private int syllable_count = 0;
    private int polysyllable_count = 0;
    private String mode;
    ReadabilityEngine(String inFileName) {
        try {
            this.text = readFileAsString(String.valueOf(Paths.get(inFileName)));
            //System.out.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        counter();
        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        this.mode = new Scanner(System.in).nextLine();
        run();
    }

    private void run() {

        switch(mode) {
            case "ARI":
                Automated_Readability_Index();
                break;
            case "FK":
                Flesch_Kincaid_index();
                break;
            case "SMOG":
                SMOG_index();
                break;
            case "CL":
                Coleman_Liau_index();
                break;
            case "all":
                double avg_age = Automated_Readability_Index() + Flesch_Kincaid_index() + SMOG_index() + Coleman_Liau_index() / 4;
                System.out.printf("This text should be understood in average by %.2f -year-olds.\n", avg_age);
        }
    }

    private String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
    /*
    private int syllable_counter(String s) {
        LinkedList<Integer> vowels=new LinkedList<>();
        for(int i = 0; i < s.length(); i++) {
            int c = s.charAt(i);
            if(c == 97 || c == 105 || c == 111 || c == 117 || c == 121 || c == 65 || c == 73 || c == 79 || c == 85 || c == 89) {
                if(!vowels.contains(c))
                    vowels.add(c);
            } else if( c == 101 || c == 69 && i != s.length() - 1) { //e is counted only if it isn't last letter
                if(!vowels.contains(c))
                    vowels.add(c);
            }
        }
        if(vowels.size() == 0)
            return 1;
        else
            return vowels.size();
    }
     */
    //for each word
    private int syllable_counter(String word) {
        ArrayList<String> tokens = new ArrayList<String>();
        String regexp = "[bcdfghjklmnpqrstvwxz]*[aeiouy]+[bcdfghjklmnpqrstvwxz]*";
        Pattern p = Pattern.compile(regexp);
        Matcher m = p.matcher(word.toLowerCase());

        while (m.find()) {
            tokens.add(m.group());
        }

        //check if e is at last and e is not the only vowel or not
        if( tokens.size() > 1 && tokens.get(tokens.size()-1).equals("e")  )
            return tokens.size()-1; // e is at last and not the only vowel so total syllable -1
        return tokens.size();
    }

    private int FindAge(double score) {
        int final_score = (int) Math.ceil(score);
        switch (final_score) {
            case 1:
                return 6;
            case 2:
                return 7;
            case 3:
                return 9;
            case 4:
                return 10;
            case 5:
                return 11;
            case 6:
                return 12;
            case 7:
                return 13;
            case 8:
                return 14;
            case 9:
                return 15;
            case 10:
                return 16;
            case 11:
                return 17;
            case 12:
                return 18;
            case 13:
                return 19;
            default :
                return 24;
        }
    }

    private int Flesch_Kincaid_index() {
        double score = (0.39 * word_count / sentence_count + 11.8 * syllable_count / word_count - 15.59);
        int age = FindAge(score);
        System.out.printf("Flesch–Kincaid readability tests: %.2f (about %d-year-olds).\n",score, age);
        return age;
    }

    private int SMOG_index() {
        double score = 1.043 * Math.sqrt((float)polysyllable_count * 30 / (float)sentence_count) + 3.1291;
        int age = FindAge(score);
        System.out.printf("Simple Measure of Gobbledygook: %.2f (about %d-year-olds).\n",score, age);
        return age;
    }

    private int Coleman_Liau_index() {
        double score = 0.0588 * char_count / word_count * 100 - 0.296 * sentence_count / word_count * 100 - 15.8;
        int age = FindAge(score);
        System.out.printf("Coleman–Liau index: %.2f (about %d-year-olds).\n",score, age);
        return age;
    }

    private int Automated_Readability_Index() {
        double score = (4.71 * char_count / word_count + 0.5 * word_count / sentence_count - 21.43);
        int age = FindAge(score);
        System.out.printf("Automated Readability Index: %.2f (about %d-year-olds).\n",score, age);
        return age;
    }

    private void counter() {
        String[] sentences = text.split("[.!?]");
        String[] words;
        sentence_count = sentences.length;
        for (String sentence : sentences) {
            sentence = sentence.trim();
            words = sentence.split("[ ]+");
            for (String word : words) {
                syllable_count += syllable_counter(word);
                if(syllable_counter(word) > 2)
                    polysyllable_count++;
                char_count += word.length();
            }
            word_count += words.length;
        }
        String chars = text.replaceAll("[ \\n\\t]+", "");
        char_count = chars.length();
        System.out.println("The text is:\n" + text + "\nWords: " + word_count + "\nSentences: " + sentence_count + "\nCharacters: " + char_count + "\nSyllables: " + syllable_count + "\nPolysyllables: " + polysyllable_count);
    }
}

public class Main {
    public static void main(String[] args) {
        //System.out.println("start");
        new ReadabilityEngine(args[0]);
    }
}
