/**
 *
 */
package com.yanry.testdriver.ui.mobile.model.base.view;

import com.yanry.testdriver.ui.mobile.model.base.Path;
import com.yanry.testdriver.ui.mobile.model.base.Presentable;
import com.yanry.testdriver.ui.mobile.model.base.window.Window;

/**
 * @author yanry
 *         <p>
 *         Jan 10, 2017
 */
@Presentable
public class View {
    private ViewContainer parent;
    private Object tag;

    public View(ViewContainer parent, Object tag) {
        this.parent = parent;
        this.tag = tag;
    }

    public void present(Path path) {
        present(path, parent);
    }

    private void present(Path path, ViewContainer container) {
        if (container instanceof Tab) {
            Tab tab = (Tab) container;
            path.put(tab.getCurrentTab(), tab);
            present(path, tab.getParent());
        }
    }

    public Window getWindow() {
        if (parent instanceof Window) {
            return (Window) parent;
        } else if (parent instanceof Tab) {
            return ((Tab) parent).getWindow();
        }
        return null;
    }

    @Presentable
    public ViewContainer getParent() {
        return parent;
    }

    @Presentable
    public Object getTag() {
        return tag;
    }
}
