getprop dalvik.vm.heapsize

bindService(intent, connection, Service.BIND_AUTO_CREATE);@MainActivity
	mBase.bindService(service, conn, flags);@ContextWrapper
		bindServiceCommon(service, conn, flags, null, mMainThread.getHandler(), null, getUser()); @ContextImpl
			IServiceConnection sd;
			sd = mPackageInfo.getServiceDispatcher(conn, getOuterContext(), executor, flags);
				getServiceDispatcherCommon(c, context, null, executor, flags);@LoadedApk
					sd = new ServiceDispatcher(c, context, executor, flags);
						mIServiceConnection = new InnerConnection(this);	// this重要
							mDispatcher = new WeakReference<LoadedApk.ServiceDispatcher>(sd);
							connected(ComponentName name, IBinder service, boolean dead)
								sd.connected(name, service, dead);
									doConnected(name, service, dead);
										mConnection.onServiceConnected(name, service);
										mConnection.onServiceDisconnected(name);

			int res = ActivityManager.getService().bindIsolatedService(
                mMainThread.getApplicationThread(), getActivityToken(), service,
                service.resolveTypeIfNeeded(getContentResolver()),
                sd, flags, instanceName, getOpPackageName(), user.getIdentifier());
				ActivityManager.getService()
					IActivityManagerSingleton.get();	==> 返回AMS代理
						create()
							IBinder b = ServiceManager.getService(Context.ACTIVITY_SERVICE);
							IActivityManager am = IActivityManager.Stub.asInterface(b);

						bindIsolatedService(caller, token, service, resolvedType, connection, flags,
                			null, callingPackage, userId); @ActivityManagerService
							mServices.bindServiceLocked(caller, token, service,
                    			resolvedType, connection, flags, instanceName, callingPackage, userId);
								bringUpServiceLocked()
									app = mAm.getProcessRecordLocked(procName, r.appInfo.uid);
									1.进程已启动
									realStartServiceLocked(r, app, thread, pid, uidRecord, execInFg, enqueueOomAdj);
										requestServiceBindingsLocked()
											requestServiceBindingLocked()
												r.app.getThread().scheduleBindService(r, i.intent.getIntent(), rebind, r.app.mState.getReportedProcState());
													sendMessage(H.BIND_SERVICE, s);@ActivityThread
														handleBindService((BindServiceData)msg.obj);
															IBinder binder = s.onBind(data.intent);// 目标Service的onBind回调
															ActivityManager.getService().publishService(data.token, data.intent, binder);
																mServices.publishServiceLocked((ServiceRecord)token, intent, service);@AMS
																	c.conn.connected(r.name, service, false);	// 回调onServiceConnected line12-15
									2.进程未启动
									app = mAm.startProcessLocked(procName, r.appInfo, true, intentFlags, hostingRecord, ZYGOTE_POLICY_FLAG_EMPTY, false, isolated)
										mProcessList.startProcessLocked();@AMS
											final String entryPoint = "android.app.ActivityThread";
											return startProcessLocked(hostingRecord, entryPoint, app, uid, gids,
                    							runtimeFlags, zygotePolicyFlags, mountExternal, seInfo, requiredAbi,
                    							instructionSet, invokeWith, startTime);
												Process.ProcessStartResult startResult = startProcess(hostingRecord, entryPoint, app, uid, gids, runtimeFlags, zygotePolicyFlags, mountExternal, seInfo,
						                        	requiredAbi, instructionSet, invokeWith, startTime);
													// zygote 启动进程最终调用
													LocalSocket usapSessionSocket = zygoteState.getUsapSessionSocket();
													ufferedWriter usapWriter =  BufferedWriter(new OutputStreamWriter(usapSessionSocket.getOutputStream()), Zygote.SOCKET_BUFFER_SIZE);
													DataInputStream usapReader = new DataInputStream(usapSessionSocket.getInputStream());
													usapWriter.write(msgStr);
													usapWriter.flush();
													Process.ProcessStartResult result = new Process.ProcessStartResult();
													result.pid = usapReader.readInt(); // 从输入流得到新进程pid;

						                		handleProcessStartedLocked(app, startResult.pid, startResult.usingWrapper, startSeq, false);
						        // 通过上面创建进程
						        if (s.app != null) // 成立
						        mAm.updateLruProcessLocked();





