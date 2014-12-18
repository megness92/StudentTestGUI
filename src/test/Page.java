package test;

import javafx.scene.control.TreeItem;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;

import javafx.collections.ObservableList;

/**
 * Page
 *
 */
public abstract class Page extends TreeItem<String> {

    protected Page(String name) {
        super(name);
    }

    public void setName(String name){
        setValue(name);
    }

    public String getName() {
        return getValue();
    }

    public String getPath() {
        if (getParent() == null) {
            return getName();
        } else {
            String parentsPath = ((Page)getParent()).getPath();
            if (parentsPath.equalsIgnoreCase("All")) {
                return getName();
            } else {
                return  parentsPath + "/" + getName();
            }
        }
    }

    public abstract Node createView();

    public Page getChild(String path) {
        int firstIndex = path.indexOf('/');
        String childName = (firstIndex==-1) ? path : path.substring(firstIndex + 1, path.length());
        if(childName.equals("Test")) {return TSStudentMain.getTSStudentMain().getPages().getTest();}
       
        return null;
    }

    @Override public String toString() {
        return toString("");
    }

    private String toString(String indent) {
        String out = indent + "["+getName()+"] "+getClass().getSimpleName();
        ObservableList<TreeItem<String>> childModel = getChildren();
        if (childModel!=null) {
            for (TreeItem child:childModel) {
                out += "\n"+((Page)child).toString("    "+indent);
            }
        }
        return out;
    }

    public static class GoToPageEventHandler implements EventHandler {
        private String pagePath;

        public GoToPageEventHandler(String pagePath) {
            this.pagePath = pagePath;
        }

        public void handle(Event event) {
            TSStudentMain.getTSStudentMain().goToPage(pagePath);
        }
    }
}
