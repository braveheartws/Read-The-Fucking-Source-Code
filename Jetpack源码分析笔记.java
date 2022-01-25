.基本使用
// 在Activity创建成员变量
private var observer:ObserverImpl ? = null
// 在Activity#onCreate()方法添加监听
observer = ObserverImpl()
// 注册观察者
lifecycle.addObserver(observer!!)

// 自定义类实现LifecycleObserver接口
class ObserverImpl :LifecycleObserver {
	// 通过注解绑定函数，反射调用该方法
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        println("onCreate")
    }
}

2.注册流程
onCreate@Activity
	addObserver@LifecycleRegistry
		ObserverWithState statefulObserver = new ObserverWithState(observer, initialState);
			mLifecycleObserver = Lifecycling.lifecycleEventObserver(observer);
				int type = getObserverConstructorType(klass);
					int type = resolveObserverCallbackType(klass);	--> REFLECTIVE_CALLBACK
						// 这里返回空因为不是GeneratedAdapter类型
						Constructor<? extends GeneratedAdapter> constructor = generatedConstructor(klass);
				        if (constructor != null) {
				            sClassToAdapters.put(klass, Collections
				                    .<Constructor<? extends GeneratedAdapter>>singletonList(constructor));
				            return GENERATED_CALLBACK;
				        }
				        // 这里遍历了这个class的所有方法，方法里面是否包含有@OnLifecycleEven这类注解
						boolean hasLifecycleMethods = ClassesInfoCache.sInstance.hasLifecycleMethods(klass);
				return new ReflectiveGenericLifecycleObserver(object);
					// mInfo 里面获取了带有注解的函数
					mInfo = ClassesInfoCache.sInstance.getInfo(mWrapped.getClass());
			// LifecycleEventObserver mLifecycleObserver = new ReflectiveGenericLifecycleObserver();

		State targetState = calculateTargetState(observer);	// INITIALIZED
		while ((statefulObserver.mState.compareTo(targetState) < 0	//如果观察者和被观察者的状态不一致则一直遍历让其生命周期同步
                && mObserverMap.contains(observer))) {
            pushParentState(statefulObserver.mState);
            statefulObserver.dispatchEvent(lifecycleOwner, upEvent(statefulObserver.mState));	//upEvent -> return ON_CREATE;
            popParentState();
            // mState / subling may have been changed recalculate
            targetState = calculateTargetState(observer);
        }

        dispatchEvent@ReflectiveGenericLifecycleObserver
        	State newState = getStateAfter(event);	--> return CREATED;
        	// mInfo在ReflectiveGenericLifecycleObserver构造的时候就拿到了所有用OnLifecycleEvent标记的方法
        	// 所以可以通过反射调用到观察者的监听生命周期函数
            mInfo.invokeCallbacks(source, event, mWrapped);	
            	mMethod.invoke(target, source, event);

3. 触发绑定的函数
ComponentActivity#onCreate()
	ReportFragment.injectIfNeededIn(this);	// 通过ReportFragment的生命周期来监听Activity的生命周期

