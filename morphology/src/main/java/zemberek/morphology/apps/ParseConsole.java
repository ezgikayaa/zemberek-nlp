package zemberek.morphology.apps;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import zemberek.core.logging.Log;
import zemberek.core.turkish.PrimaryPos;
import zemberek.core.turkish.RootAttribute;
import zemberek.morphology.analysis.SingleAnalysis;
import zemberek.morphology.TurkishMorphology;
import zemberek.morphology.lexicon.RootLexicon;
import zemberek.morphology.lexicon.tr.TurkishDictionaryLoader;

public class ParseConsole {

  public static RootLexicon addTextDictionaryResources(String... resources) throws IOException {
    RootLexicon lexicon = new RootLexicon();
    Log.info("Dictionaries :%s", String.join(", ", Arrays.asList(resources)));
    List<String> lines = new ArrayList<>();
    for (String resource : resources) {
      lines.addAll(Resources.readLines(Resources.getResource(resource), Charsets.UTF_8));
    }
    lexicon.addAll(new TurkishDictionaryLoader().load(lines));
    Log.info("Lexicon Generated.");
    return lexicon;
  }

  public static void main(String[] args) throws IOException {
    // to test the development lexicon, use ParseConsoleTest
    //TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
    TurkishMorphology morphology = TurkishMorphology.builder().addDefaultDictionaries().build();
    //morphology.getGraph().stats();
    new ParseConsole().run(morphology);
  }

  public void run(TurkishMorphology parser) throws IOException {
    String input;
    System.out.println("Enter word:");
    Scanner sc = new Scanner(System.in);
    input = sc.nextLine();
    while (!input.equals("exit") && !input.equals("quit")) {

      List<SingleAnalysis> tokens = parser.analyze(input).getAnalysisResults();
      if (tokens.size() == 0 || (tokens.size() == 1
          && tokens.get(0).getDictionaryItem().primaryPos == PrimaryPos.Unknown)) {
        System.out.println("cannot be parsed");
      } else {
        tokens.forEach(this::printMorphParse);
      }
      input = sc.nextLine();
    }
  }

  protected void printMorphParse(SingleAnalysis token) {
    String runtime =
        token.getDictionaryItem().hasAttribute(RootAttribute.Runtime) ? " [Not in dictionary]" : "";
    System.out.println(token.formatLong() + runtime);
  }
}
