package com.yanry.driver.core.model.runtime.revert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class RevertibleLinkedList<E> {
    private RevertManager manager;
    private LinkedList<E> list;

    public RevertibleLinkedList(RevertManager manager) {
        this.manager = manager;
        list = new LinkedList<>();
    }

    public void addLast(E e) {
        manager.proceed(new Revertible() {
            @Override
            public void proceed() {
                list.addLast(e);
            }

            @Override
            public void recover() {
                list.removeLast();
            }
        });
    }

    public void addAll(Collection<E> collection) {
        int count = collection.size();
        if (count > 0) {
            manager.proceed(new Revertible() {
                @Override
                public void proceed() {
                    list.addAll(collection);
                }

                @Override
                public void recover() {
                    for (int i = 0; i < count; i++) {
                        list.removeLast();
                    }
                }
            });
        }
    }

    public boolean remove(E element) {
        LinkedList<E> copy = new LinkedList<>(list);
        if (list.remove(element)) {
            manager.proceed(new Revertible() {
                @Override
                public void proceed() {
                }

                @Override
                public void recover() {
                    list = copy;
                }
            });
            return true;
        }
        return false;
    }

    public void removeAll(Collection<E> collection) {
        if (collection.size() > 0) {
            LinkedList<E> copy = new LinkedList<>(list);
            manager.proceed(new Revertible() {
                @Override
                public void proceed() {
                    list.removeAll(collection);
                }

                @Override
                public void recover() {
                    list = copy;
                }
            });
        }
    }

    public void clear() {
        if (list.size() > 0) {
            ArrayList<E> copy = new ArrayList<>(list);
            manager.proceed(new Revertible() {
                @Override
                public void proceed() {
                    list.clear();
                }

                @Override
                public void recover() {
                    list.addAll(copy);
                }
            });
        }
    }

    public Iterator<E> iterator() {
        LinkedList<E> copy = new LinkedList<>(list);
        manager.proceed(new Revertible() {
            @Override
            public void proceed() {
            }

            @Override
            public void recover() {
                list = copy;
            }
        });
        return list.iterator();
    }

    public ArrayList<E> getList() {
        return new ArrayList<>(list);
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean contains(E element) {
        return list.contains(element);
    }
}
