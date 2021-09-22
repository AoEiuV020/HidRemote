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

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by yuyidong on 15/9/30.
 */
class DragListView extends ListView {
    /* 监听器 */
    private SlideAndDragListView.OnDragDropListener mOnDragDropListener;
    /* 监听器 */
    private Callback.OnDragDropListener mAdapterDragDropListener;
    private Callback.OnDragDropListener mDragListDragDropListener;
    /* drag */
    public DragManager mDragManager;

    public DragListView(Context context) {
        this(context, null);
    }

    public DragListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewGroup decorView = null;
        if (context instanceof Activity) {
            decorView = (ViewGroup) ((Activity) context).getWindow().getDecorView();
        }
        mDragManager = new DragManager(context, this, decorView);
    }

    protected void setDragPosition(int position) {
        View view = getChildAt(position - getFirstVisiblePosition());
        if (mOnDragDropListener != null && view instanceof ItemMainLayout) {
            ItemMainLayout itemMainLayout = (ItemMainLayout) getChildAt(position - getFirstVisiblePosition());
            itemMainLayout.getItemLeftBackGroundLayout().setVisibility(GONE);
            itemMainLayout.getItemRightBackGroundLayout().setVisibility(GONE);
            mDragManager.setDragging(true);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragManager.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDragManager.onTouchEvent(event) || super.onTouchEvent(event);
    }

    /**
     * 设置drag的监听器，加入数据
     *
     * @param onDragDropListener
     */
    public void setOnDragDropListener(SlideAndDragListView.OnDragDropListener onDragDropListener) {
        mOnDragDropListener = onDragDropListener;
    }

    private View getViewByPoint(int x, int y) {
        int count = getChildCount();
        View child;
        for (int childIdx = 0; childIdx < count; childIdx++) {
            child = getChildAt(childIdx);
            if (y >= child.getTop() && y <= child.getBottom() && x >= child.getLeft() && x <= child.getRight()) {
                return child;
            }
        }
        return null;
    }

    protected void handleDragStarted(int x, int y) {
        View view = getViewByPoint(x, y);
        if (view == null) {
            return;
        }
        boolean isDragging = false;
        if (mAdapterDragDropListener != null) {
            isDragging = mAdapterDragDropListener.onDragStarted(x, y, view);
        }
        if (mDragListDragDropListener != null && isDragging) {
            mDragListDragDropListener.onDragStarted(x, y, view);
        }
        if (mOnDragDropListener != null && isDragging) {
            mOnDragDropListener.onDragViewStart(getPositionForView(view) - getHeaderViewsCount());
        }
    }

    protected void handleDragMoving(int x, int y) {
        View view = getViewByPoint(x, y);
        if (view == null) {
            return;
        }
        if (mAdapterDragDropListener != null) {
            mAdapterDragDropListener.onDragMoving(x, y, view, mOnDragDropListener);
        }
        if (mDragListDragDropListener != null) {
            mDragListDragDropListener.onDragMoving(x, y, view, null);
        }
    }

    protected void handleDragFinished(int x, int y) {
        if (mAdapterDragDropListener != null) {
            mAdapterDragDropListener.onDragFinished(x, y, mOnDragDropListener);
        }
        if (mDragListDragDropListener != null) {
            mDragListDragDropListener.onDragFinished(x, y, null);
        }
    }

    protected void setListDragDropListener(Callback.OnDragDropListener listener) {
        mDragListDragDropListener = listener;
    }

    protected void serAdapterDragDropListener(Callback.OnDragDropListener listener) {
        mAdapterDragDropListener = listener;
    }

    protected boolean isDragging() {
        return mDragManager.isDragging();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDragManager.onSizeChanged();
    }
}
