package com.mikeandv.analyzer;

import com.mikeandv.entity.TestSuit;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class Processor {
    final private static String str = "====================================================================";


    public static void main(String[] args) throws MalformedURLException {

        if (args.length == 0) {
            System.out.println("No jar-file a passed as parameters");
        }

        URL[] urls1 = new URL[args.length];

        for (int i = 0; i< args.length; i++) {
            File file = new File(args[i]);
            URL url = file.toURI().toURL();
            urls1[i] = url;
        }

        TestClassAnalyzer tca = new TestClassAnalyzer();
        List<TestSuit> testSuits = tca.analyze(urls1);


        if (tca.isError()) {

            System.out.println(tca.getError());

        } else {

            for (TestSuit suit : testSuits) {

                suit.run();
                String s = suit.getMessage() + str;
                suit.setMessage(s);
            }

        }
        System.out.println(str);
        testSuits.forEach(System.out :: println);
    }
}
