package com.xiaoke.swipedelete;

/**
 * 会帮助我们记录打开的item，提供关闭打开item的方法，提供判断当前是否可以滑动的方法，提供清除所记录item的方法
 * 另外，由于所有的item都需要能够访问打开的item，所以我们将SwipeLayoutManager
 * 设计为单例的
 * @author lxj
 *
 */
public class SwipeLayoutManager {
	private SwipeLayoutManager(){}
	private static SwipeLayoutManager mInstance = new SwipeLayoutManager();
	public static SwipeLayoutManager getInstance(){
		return mInstance;
	}
	
	private SwipeLayout openSwipeLayout;
	/**
	 * 记录已经打开的SwipeLayout
	 * @param openSwipeLayout
	 */
	public void setOpenSwipeLayout(SwipeLayout openSwipeLayout){
		this.openSwipeLayout = openSwipeLayout;
	}
	
	/**
	 * 判断当前是否能够滑动
	 * @return
	 */
	public boolean isCanSwipe(SwipeLayout currentLayout){
		if(openSwipeLayout==null){
			//说明没有打开的，那么则可以滑动
			return true;
		}else {
			//如果当前有打开的，那么判断打开的和当前触摸的是否是同一个，如果是同一个，那么可以滑动，反之不可以
			return openSwipeLayout==currentLayout;
		}
	}
	
	public void clearSwipeLayout(){
		openSwipeLayout = null;
	}
	
	/**
	 * 关闭已经打开的SwipeLayout
	 */
	public void closeSwipeLayout(){
		if(openSwipeLayout!=null){
			openSwipeLayout.close();
		}
	}
}
