/*
 * Copyright (C) 2015 yydcdut (yuyidong2015@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.yydcdut.sdlv;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuyidong on 15/10/8.
 */
public final class Menu {
    public static final int ITEM_NOTHING = 0;
    public static final int ITEM_SCROLL_BACK = 1;
    public static final int ITEM_DELETE_FROM_BOTTOM_TO_TOP = 2;

    private List<MenuItem> mLeftMenuItems;
    private List<MenuItem> mRightMenuItems;

    private boolean isSlideOver = true;

    private int mMenuViewType = 0;


    public Menu(boolean slideOver) {
        this(slideOver, 0);
    }

    public Menu(boolean slideOver, int menuViewType) {
        isSlideOver = slideOver;
        mLeftMenuItems = new ArrayList<>();
        mRightMenuItems = new ArrayList<>();
        mMenuViewType = menuViewType;
    }

    protected boolean isSlideOver() {
        return isSlideOver;
    }

    public void addItem(MenuItem menuItem) {
        if (menuItem.direction == MenuItem.DIRECTION_LEFT) {
            mLeftMenuItems.add(menuItem);
        } else {
            mRightMenuItems.add(menuItem);
        }
    }

    public void addItem(MenuItem menuItem, int position) {
        if (menuItem.direction == MenuItem.DIRECTION_LEFT) {
            mLeftMenuItems.add(position, menuItem);
        } else {
            mRightMenuItems.add(position, menuItem);
        }
    }

    public boolean removeItem(MenuItem menuItem) {
        if (menuItem.direction == MenuItem.DIRECTION_LEFT) {
            return mLeftMenuItems.remove(menuItem);
        } else {
            return mRightMenuItems.remove(menuItem);
        }
    }

    protected int getTotalBtnLength(int direction) {
        int total = 0;
        if (direction == MenuItem.DIRECTION_LEFT) {
            for (MenuItem menuItem : mLeftMenuItems) {
                total += menuItem.width;
            }
            return total;
        } else {
            for (MenuItem menuItem : mRightMenuItems) {
                total += menuItem.width;
            }
            return total;
        }
    }

    /**
     * 这个函数并不是十分安全，因为获取到List之后自己操作add或者remove的话btn总长度不会有操作变化
     *
     * @param direction
     * @return
     */
    protected List<MenuItem> getMenuItems(int direction) {
        if (direction == MenuItem.DIRECTION_LEFT) {
            return mLeftMenuItems;
        } else {
            return mRightMenuItems;
        }
    }

    public int getMenuViewType() {
        return mMenuViewType;
    }
}
