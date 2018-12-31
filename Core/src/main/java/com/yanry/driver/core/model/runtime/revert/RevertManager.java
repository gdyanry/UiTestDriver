package com.yanry.driver.core.model.runtime.revert;

import java.util.HashMap;
import java.util.LinkedList;

public class RevertManager {
    private LinkedList<Revertible> stack;
    private HashMap<Object, TagStep> tags;

    public RevertManager() {
        stack = new LinkedList<>();
        tags = new HashMap<>();
    }

    public void proceed(Revertible step) {
        if (!stack.isEmpty()) {
            stack.push(step);
        }
        step.proceed();
    }

    public void revert() {
        Revertible step;
        while ((step = stack.pop()) != null) {
            if (!(step instanceof TagStep)) {
                step.recover();
                return;
            }
        }
    }

    public void tag(Object tag) {
        if (tag == null) {
            return;
        }
        TagStep tagStep = tags.get(tag);
        if (tagStep == null) {
            tagStep = new TagStep(tag);
            tags.put(tag, tagStep);
        }
        stack.push(tagStep);
    }

    public void revertTo(Object tag) {
        Revertible step;
        while ((step = stack.pop()) != null) {
            if (step instanceof TagStep) {
                TagStep tagStep = (TagStep) step;
                if (tagStep.tag.equals(tag)) {
                    return;
                }
            } else {
                step.recover();
            }
        }
    }

    public int getTagCount() {
        int count = 0;
        for (Revertible step : stack) {
            if (step instanceof TagStep) {
                count++;
            }
        }
        return count;
    }

    public void clean() {
        stack.clear();
    }

    private static class TagStep implements Revertible {
        private Object tag;

        private TagStep(Object tag) {
            this.tag = tag;
        }

        @Override
        public void proceed() {

        }

        @Override
        public void recover() {

        }
    }
}