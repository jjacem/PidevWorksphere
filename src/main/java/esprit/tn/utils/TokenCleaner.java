package esprit.tn.utils;

import java.io.File;

public class TokenCleaner {


        private static final String TOKENS_DIRECTORY_PATH = "tokens";

        public static void cleanTokensDirectory() {
            File tokensDir = new File(TOKENS_DIRECTORY_PATH);
            if (tokensDir.exists() && tokensDir.isDirectory()) {
                File[] files = tokensDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            file.delete();  // Delete each file
                        }
                    }
                }
            } else {
                System.out.println("Tokens directory does not exist.");
            }
        }

        public static void main(String[] args) {
            cleanTokensDirectory();
        }
    }

