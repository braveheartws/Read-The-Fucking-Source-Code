FragmentManagerImpl#void moveToState(Fragment f, int newState, int transit, int transitionStyle,
                     boolean keepActive){
    case Fragment.CREATED:
        container = (ViewGroup) mContainer.onFindViewById(f.mContainerId);  == return FragmentActivity.this.findViewById(id);
            getWindow().findViewById(id);

}
    


Activity#onCreate()
    mFragments.attachHost(null /*parent*/);
        mHost.mFragmentManager.attachController(mHost, mHost /*container*/, parent); // mHost = FragmentHostCallback<?>


supportFragmentManager.beginTransaction().replace(R.id.frameLayout, MyFragment(R.layout.activity_main)).commit()
    commitInternal(false);
        mManager.enqueueAction(this, allowStateLoss);
            scheduleCommit();
                mHost.getHandler().post(mExecCommit);
                    execPendingActions();
                        doPendingDeferredStart();
                            startPendingDeferredFragments();
                                performPendingDeferredStart(f);
                                    moveToState(f, mCurState, 0, 0, false);
                                        case Fragment.INITIALIZING: // 第一次commit时这个状态是初始状态 INITIALIZING
                                            f.mFragmentManager = mParent != null ? mParent.mChildFragmentManager : mHost.mFragmentManager;
                                            dispatchOnFragmentPreAttached(f, mHost.getContext(), false);
                                            // call Fragment#attach
                                            f.performAttach();
                                                onAttach(mHost.getContext());
                                            f.mParentFragment.onAttachFragment(f);
                                            dispatchOnFragmentAttached(f, mHost.getContext(), false);
                                            if (!f.mIsCreated) {
                                                dispatchOnFragmentPreCreated(f, f.mSavedFragmentState, false);
                                                f.performCreate(f.mSavedFragmentState);
                                                    mState = CREATED;   // 改变状态
                                                    mSavedStateRegistryController.performRestore(savedInstanceState); // 数据恢复操作
                                                    onCreate(savedInstanceState);   // 执行onCreate方法
                                                    mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
                                                dispatchOnFragmentCreated(f, f.mSavedFragmentState, false);
                                            }
                                        case Fragment.CREATED:
                                            ViewGroup container = null;
                                            if (f.mContainerId != 0) {
                                                container = (ViewGroup) mContainer.onFindViewById(f.mContainerId);
                                                 FragmentActivity.this.findViewById(id);
                                            }
                                            f.mContainer = container;
                                            f.performCreateView(f.performGetLayoutInflater(f.mSavedFragmentState), container, f.mSavedFragmentState);
                                                mView = onCreateView(inflater, container, savedInstanceState);
                                            f.onViewCreated(f.mView, f.mSavedFragmentState);
                                            f.performActivityCreated(f.mSavedFragmentState);
                                                onActivityCreated(savedInstanceState);
                                        case Fragment.ACTIVITY_CREATED:
                                            f.performStart();
                                        case Fragment.STARTED:
                                            f.performResume();
                                            