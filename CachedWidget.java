package mainFiles;

import org.osbot.rs07.api.Widgets;
import org.osbot.rs07.api.ui.RS2Widget;

public class CachedWidget {

    private int parentID = -1, childID = -1, subChildID = -1;
    private String[] widgetTexts;

    public CachedWidget(final int parentID, final int childID){
        if(parentID < 0 || childID < 0) throw new IllegalArgumentException("Widget IDs must have a value > 0");
        this.parentID = parentID;
        this.subChildID = childID;
    }

    public CachedWidget(final int parentID, final int childID, final int subChildID){
        if(parentID < 0 || childID < 0 || subChildID < 0) throw new IllegalArgumentException("Widget IDs must have a value > 0");
        this.parentID = parentID;
        this.childID = childID;
        this.subChildID = subChildID;
    }

    public CachedWidget(final String... widgetTexts){
        if(widgetTexts.length == 0) throw new IllegalArgumentException("At least 1 String must be provided");
        this.widgetTexts = widgetTexts;
    }

    public RS2Widget get(final Widgets widgets){
        if(subChildID != -1) return widgets.get(parentID, childID, subChildID);
        else if(parentID != -1) return getSecondLevelWidget(widgets);
        else return getWidgetWithText(widgets);
    }

    private RS2Widget getSecondLevelWidget(final Widgets widgets){
        RS2Widget rs2Widget = widgets.get(parentID, childID);
        if(rs2Widget != null && rs2Widget.isThirdLevel()){
            subChildID = rs2Widget.getThirdLevelId();
        }
        return rs2Widget;
    }

    private RS2Widget getWidgetWithText(final Widgets widgets){
        RS2Widget rs2Widget = widgets.getWidgetContainingText(widgetTexts);
        if(rs2Widget != null){
            parentID = rs2Widget.getRootId();
            childID = rs2Widget.getSecondLevelId();
            if(rs2Widget.isThirdLevel()) subChildID = rs2Widget.getThirdLevelId();
        }
        return rs2Widget;
    }
} 