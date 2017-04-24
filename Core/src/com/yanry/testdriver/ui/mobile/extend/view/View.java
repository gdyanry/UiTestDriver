/**
 *
 */
package com.yanry.testdriver.ui.mobile.extend.view;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;
import com.yanry.testdriver.ui.mobile.extend.window.Visibility;
import com.yanry.testdriver.ui.mobile.extend.window.Window;

/**
 * @author yanry
 *         <p>
 *         Jan 10, 2017
 */
@Presentable
public class View {
    private ViewContainer parent;
    private ViewSelector selector;

    public View(ViewContainer parent, ViewSelector selector) {
        this.parent = parent;
        this.selector = selector;
    }

    public void present(Path path) {
        present(path, parent);
    }

    private void present(Path path, ViewContainer container) {
        if (container instanceof Tab) {
            Tab tab = (Tab) container;
            path.put(tab.getCurrentTab(), tab);
            present(path, tab.getParent());
        } else if (container instanceof Window) {
            path.put(((Window)container).getState(), Visibility.Foreground);
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
    public Object getSelector() {
        return selector;
    }
}
