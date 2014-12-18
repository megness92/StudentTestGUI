package test;

import test.pages.AllPagesPage;
import test.pages.CategoryPage;
import test.panels.*;

/**
 * Pages
 *
 */
public class Pages {
    public static final String TEST = "Test";

    private AllPagesPage root;
    private CategoryPage test;
    private PassTestPage passTest;
    private InstructionPage testIns;
    
    public Pages() {
        root = new AllPagesPage();
        test = new CategoryPage(TEST);
        root.getChildren().add(test);

    }

    public void parseSamples(){
        passTest = new PassTestPage("Pass test");
        testIns = new InstructionPage("Instructions");
        test.getChildren().add(testIns);
        test.getChildren().add(passTest);
    }

    public Page getPage(String name) {
        Page page = root.getChild(name);
        return page;
    }

    public Page getTest() { return test; }

    public Page getRoot() {
        return root;
    }
    
    public PassTestPage getPTPage() {
        return passTest;
    }
    
    public InstructionPage getInsPage() {
        return testIns;
    }
}
