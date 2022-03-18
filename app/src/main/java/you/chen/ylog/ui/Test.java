package you.chen.ylog.ui;

/**
 * author: you : 2022/3/18
 */
public interface Test {

    String log = "/*\n" +
            " * Copyright (C) 2015 The Android Open Source Project\n" +
            " *\n" +
            " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            " * you may not use this file except in compliance with the License.\n" +
            " * You may obtain a copy of the License at\n" +
            " *\n" +
            " *      http://www.apache.org/licenses/LICENSE-2.0\n" +
            " *\n" +
            " * Unless required by applicable law or agreed to in writing, software\n" +
            " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            " * See the License for the specific language governing permissions and\n" +
            " * limitations under the License.\n" +
            " */\n" +
            "\n" +
            "package androidx.appcompat.app;\n" +
            "\n" +
            "import android.content.Context;\n" +
            "import android.content.Intent;\n" +
            "import android.content.res.Configuration;\n" +
            "import android.content.res.Resources;\n" +
            "import android.os.Build;\n" +
            "import android.os.Bundle;\n" +
            "import android.util.DisplayMetrics;\n" +
            "import android.view.KeyEvent;\n" +
            "import android.view.Menu;\n" +
            "import android.view.MenuInflater;\n" +
            "import android.view.View;\n" +
            "import android.view.ViewGroup;\n" +
            "import android.view.Window;\n" +
            "\n" +
            "import androidx.annotation.CallSuper;\n" +
            "import androidx.annotation.ContentView;\n" +
            "import androidx.annotation.IdRes;\n" +
            "import androidx.annotation.LayoutRes;\n" +
            "import androidx.annotation.NonNull;\n" +
            "import androidx.annotation.Nullable;\n" +
            "import androidx.annotation.StyleRes;\n" +
            "import androidx.appcompat.app.AppCompatDelegate.NightMode;\n" +
            "import androidx.appcompat.view.ActionMode;\n" +
            "import androidx.appcompat.widget.Toolbar;\n" +
            "import androidx.appcompat.widget.VectorEnabledTintResources;\n" +
            "import androidx.core.app.ActivityCompat;\n" +
            "import androidx.core.app.NavUtils;\n" +
            "import androidx.core.app.TaskStackBuilder;\n" +
            "import androidx.fragment.app.FragmentActivity;\n" +
            "\n" +
            "/**\n" +
            " * Base class for activities that use the\n" +
            " * <a href=\"{@docRoot}tools/extras/support-library.html\">support library</a> action bar features.\n" +
            " *\n" +
            " * <p>You can add an {@link androidx.appcompat.app.ActionBar} to your activity when running on API level 7 or higher\n" +
            " * by extending this class for your activity and setting the activity theme to\n" +
            " * {@link androidx.appcompat.R.style#Theme_AppCompat Theme.AppCompat} or a similar theme.\n" +
            " *\n" +
            " * <div class=\"special reference\">\n" +
            " * <h3>Developer Guides</h3>\n" +
            " *\n" +
            " * <p>For information about how to use the action bar, including how to add action items, navigation\n" +
            " * modes and more, read the <a href=\"{@docRoot}guide/topics/ui/actionbar.html\">Action\n" +
            " * Bar</a> API guide.</p>\n" +
            " * </div>\n" +
            " */\n" +
            "public class AppCompatActivity extends FragmentActivity implements AppCompatCallback,\n" +
            "        TaskStackBuilder.SupportParentable, ActionBarDrawerToggle.DelegateProvider {\n" +
            "\n" +
            "    private AppCompatDelegate mDelegate;\n" +
            "    private Resources mResources;\n" +
            "\n" +
            "    /**\n" +
            "     * Default constructor for AppCompatActivity. All Activities must have a default constructor\n" +
            "     * for API 27 and lower devices or when using the default\n" +
            "     * {@link android.app.AppComponentFactory}.\n" +
            "     */\n" +
            "    public AppCompatActivity() {\n" +
            "        super();\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Alternate constructor that can be used to provide a default layout\n" +
            "     * that will be inflated as part of <code>super.onCreate(savedInstanceState)</code>.\n" +
            "     *\n" +
            "     * <p>This should generally be called from your constructor that takes no parameters,\n" +
            "     * as is required for API 27 and lower or when using the default\n" +
            "     * {@link android.app.AppComponentFactory}.\n" +
            "     *\n" +
            "     * @see #AppCompatActivity()\n" +
            "     */\n" +
            "    @ContentView\n" +
            "    public AppCompatActivity(@LayoutRes int contentLayoutId) {\n" +
            "        super(contentLayoutId);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    protected void attachBaseContext(Context newBase) {\n" +
            "        super.attachBaseContext(newBase);\n" +
            "        getDelegate().attachBaseContext(newBase);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    protected void onCreate(@Nullable Bundle savedInstanceState) {\n" +
            "        final AppCompatDelegate delegate = getDelegate();\n" +
            "        delegate.installViewFactory();\n" +
            "        delegate.onCreate(savedInstanceState);\n" +
            "        super.onCreate(savedInstanceState);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void setTheme(@StyleRes final int resId) {\n" +
            "        super.setTheme(resId);\n" +
            "        getDelegate().setTheme(resId);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    protected void onPostCreate(@Nullable Bundle savedInstanceState) {\n" +
            "        super.onPostCreate(savedInstanceState);\n" +
            "        getDelegate().onPostCreate(savedInstanceState);\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Support library version of {@link android.app.Activity#getActionBar}.\n" +
            "     *\n" +
            "     * <p>Retrieve a reference to this activity's ActionBar.\n" +
            "     *\n" +
            "     * @return The Activity's ActionBar, or null if it does not have one.\n" +
            "     */\n" +
            "    @Nullable\n" +
            "    public ActionBar getSupportActionBar() {\n" +
            "        return getDelegate().getSupportActionBar();\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Set a {@link android.widget.Toolbar Toolbar} to act as the\n" +
            "     * {@link androidx.appcompat.app.ActionBar} for this Activity window.\n" +
            "     *\n" +
            "     * <p>When set to a non-null value the {@link #getActionBar()} method will return\n" +
            "     * an {@link androidx.appcompat.app.ActionBar} object that can be used to control the given\n" +
            "     * toolbar as if it were a traditional window decor action bar. The toolbar's menu will be\n" +
            "     * populated with the Activity's options menu and the navigation button will be wired through\n" +
            "     * the standard {@link android.R.id#home home} menu select action.</p>\n" +
            "     *\n" +
            "     * <p>In order to use a Toolbar within the Activity's window content the application\n" +
            "     * must not request the window feature\n" +
            "     * {@link android.view.Window#FEATURE_ACTION_BAR FEATURE_SUPPORT_ACTION_BAR}.</p>\n" +
            "     *\n" +
            "     * @param toolbar Toolbar to set as the Activity's action bar, or {@code null} to clear it\n" +
            "     */\n" +
            "    public void setSupportActionBar(@Nullable Toolbar toolbar) {\n" +
            "        getDelegate().setSupportActionBar(toolbar);\n" +
            "    }\n" +
            "\n" +
            "    @NonNull\n" +
            "    @Override\n" +
            "    public MenuInflater getMenuInflater() {\n" +
            "        return getDelegate().getMenuInflater();\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void setContentView(@LayoutRes int layoutResID) {\n" +
            "        getDelegate().setContentView(layoutResID);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void setContentView(View view) {\n" +
            "        getDelegate().setContentView(view);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void setContentView(View view, ViewGroup.LayoutParams params) {\n" +
            "        getDelegate().setContentView(view, params);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void addContentView(View view, ViewGroup.LayoutParams params) {\n" +
            "        getDelegate().addContentView(view, params);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void onConfigurationChanged(@NonNull Configuration newConfig) {\n" +
            "        super.onConfigurationChanged(newConfig);\n" +
            "\n" +
            "        if (mResources != null) {\n" +
            "            // The real (and thus managed) resources object was already updated\n" +
            "            // by ResourcesManager, so pull the current metrics from there.\n" +
            "            final DisplayMetrics newMetrics = super.getResources().getDisplayMetrics();\n" +
            "            mResources.updateConfiguration(newConfig, newMetrics);\n" +
            "        }\n" +
            "\n" +
            "        getDelegate().onConfigurationChanged(newConfig);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    protected void onPostResume() {\n" +
            "        super.onPostResume();\n" +
            "        getDelegate().onPostResume();\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    protected void onStart() {\n" +
            "        super.onStart();\n" +
            "        getDelegate().onStart();\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    protected void onStop() {\n" +
            "        super.onStop();\n" +
            "        getDelegate().onStop();\n" +
            "    }\n" +
            "\n" +
            "    @SuppressWarnings(\"TypeParameterUnusedInFormals\")\n" +
            "    @Override\n" +
            "    public <T extends View> T findViewById(@IdRes int id) {\n" +
            "        return getDelegate().findViewById(id);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public final boolean onMenuItemSelected(int featureId, @NonNull android.view.MenuItem item) {\n" +
            "        if (super.onMenuItemSelected(featureId, item)) {\n" +
            "            return true;\n" +
            "        }\n" +
            "\n" +
            "        final ActionBar ab = getSupportActionBar();\n" +
            "        if (item.getItemId() == android.R.id.home && ab != null &&\n" +
            "                (ab.getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) != 0) {\n" +
            "            return onSupportNavigateUp();\n" +
            "        }\n" +
            "        return false;\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    protected void onDestroy() {\n" +
            "        super.onDestroy();\n" +
            "        getDelegate().onDestroy();\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    protected void onTitleChanged(CharSequence title, int color) {\n" +
            "        super.onTitleChanged(title, color);\n" +
            "        getDelegate().setTitle(title);\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Enable extended support library window features.\n" +
            "     * <p>\n" +
            "     * This is a convenience for calling\n" +
            "     * {@link android.view.Window#requestFeature getWindow().requestFeature()}.\n" +
            "     * </p>\n" +
            "     *\n" +
            "     * @param featureId The desired feature as defined in\n" +
            "     * {@link android.view.Window} or {@link androidx.core.view.WindowCompat}.\n" +
            "     * @return Returns true if the requested feature is supported and now enabled.\n" +
            "     *\n" +
            "     * @see android.app.Activity#requestWindowFeature\n" +
            "     * @see android.view.Window#requestFeature\n" +
            "     */\n" +
            "    public boolean supportRequestWindowFeature(int featureId) {\n" +
            "        return getDelegate().requestWindowFeature(featureId);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void supportInvalidateOptionsMenu() {\n" +
            "        getDelegate().invalidateOptionsMenu();\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void invalidateOptionsMenu() {\n" +
            "        getDelegate().invalidateOptionsMenu();\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Notifies the Activity that a support action mode has been started.\n" +
            "     * Activity subclasses overriding this method should call the superclass implementation.\n" +
            "     *\n" +
            "     * @param mode The new action mode.\n" +
            "     */\n" +
            "    @Override\n" +
            "    @CallSuper\n" +
            "    public void onSupportActionModeStarted(@NonNull ActionMode mode) {\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Notifies the activity that a support action mode has finished.\n" +
            "     * Activity subclasses overriding this method should call the superclass implementation.\n" +
            "     *\n" +
            "     * @param mode The action mode that just finished.\n" +
            "     */\n" +
            "    @Override\n" +
            "    @CallSuper\n" +
            "    public void onSupportActionModeFinished(@NonNull ActionMode mode) {\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Called when a support action mode is being started for this window. Gives the\n" +
            "     * callback an opportunity to handle the action mode in its own unique and\n" +
            "     * beautiful way. If this method returns null the system can choose a way\n" +
            "     * to present the mode or choose not to start the mode at all.\n" +
            "     *\n" +
            "     * @param callback Callback to control the lifecycle of this action mode\n" +
            "     * @return The ActionMode that was started, or null if the system should present it\n" +
            "     */\n" +
            "    @Nullable\n" +
            "    @Override\n" +
            "    public ActionMode onWindowStartingSupportActionMode(@NonNull ActionMode.Callback callback) {\n" +
            "        return null;\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Start an action mode.\n" +
            "     *\n" +
            "     * @param callback Callback that will manage lifecycle events for this context mode\n" +
            "     * @return The ContextMode that was started, or null if it was canceled\n" +
            "     */\n" +
            "    @Nullable\n" +
            "    public ActionMode startSupportActionMode(@NonNull ActionMode.Callback callback) {\n" +
            "        return getDelegate().startSupportActionMode(callback);\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * @deprecated Progress bars are no longer provided in AppCompat.\n" +
            "     */\n" +
            "    @Deprecated\n" +
            "    public void setSupportProgressBarVisibility(boolean visible) {\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * @deprecated Progress bars are no longer provided in AppCompat.\n" +
            "     */\n" +
            "    @Deprecated\n" +
            "    public void setSupportProgressBarIndeterminateVisibility(boolean visible) {\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * @deprecated Progress bars are no longer provided in AppCompat.\n" +
            "     */\n" +
            "    @Deprecated\n" +
            "    public void setSupportProgressBarIndeterminate(boolean indeterminate) {\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * @deprecated Progress bars are no longer provided in AppCompat.\n" +
            "     */\n" +
            "    @Deprecated\n" +
            "    public void setSupportProgress(int progress) {\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Support version of {@link #onCreateNavigateUpTaskStack(android.app.TaskStackBuilder)}.\n" +
            "     * This method will be called on all platform versions.\n" +
            "     *\n" +
            "     * Define the synthetic task stack that will be generated during Up navigation from\n" +
            "     * a different task.\n" +
            "     *\n" +
            "     * <p>The default implementation of this method adds the parent chain of this activity\n" +
            "     * as specified in the manifest to the supplied {@link androidx.core.app.TaskStackBuilder}. Applications\n" +
            "     * may choose to override this method to construct the desired task stack in a different\n" +
            "     * way.</p>\n" +
            "     *\n" +
            "     * <p>This method will be invoked by the default implementation of {@link #onNavigateUp()}\n" +
            "     * if {@link #shouldUpRecreateTask(android.content.Intent)} returns true when supplied with the intent\n" +
            "     * returned by {@link #getParentActivityIntent()}.</p>\n" +
            "     *\n" +
            "     * <p>Applications that wish to supply extra Intent parameters to the parent stack defined\n" +
            "     * by the manifest should override\n" +
            "     * {@link #onPrepareSupportNavigateUpTaskStack(androidx.core.app.TaskStackBuilder)}.</p>\n" +
            "     *\n" +
            "     * @param builder An empty TaskStackBuilder - the application should add intents representing\n" +
            "     *                the desired task stack\n" +
            "     */\n" +
            "    public void onCreateSupportNavigateUpTaskStack(@NonNull TaskStackBuilder builder) {\n" +
            "        builder.addParentStack(this);\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Support version of {@link #onPrepareNavigateUpTaskStack(android.app.TaskStackBuilder)}.\n" +
            "     * This method will be called on all platform versions.\n" +
            "     *\n" +
            "     * Prepare the synthetic task stack that will be generated during Up navigation\n" +
            "     * from a different task.\n" +
            "     *\n" +
            "     * <p>This method receives the {@link androidx.core.app.TaskStackBuilder} with the constructed series of\n" +
            "     * Intents as generated by {@link #onCreateSupportNavigateUpTaskStack(androidx.core.app.TaskStackBuilder)}.\n" +
            "     * If any extra data should be added to these intents before launching the new task,\n" +
            "     * the application should override this method and add that data here.</p>\n" +
            "     *\n" +
            "     * @param builder A TaskStackBuilder that has been populated with Intents by\n" +
            "     *                onCreateNavigateUpTaskStack.\n" +
            "     */\n" +
            "    public void onPrepareSupportNavigateUpTaskStack(@NonNull TaskStackBuilder builder) {\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * This method is called whenever the user chooses to navigate Up within your application's\n" +
            "     * activity hierarchy from the action bar.\n" +
            "     *\n" +
            "     * <p>If a parent was specified in the manifest for this activity or an activity-alias to it,\n" +
            "     * default Up navigation will be handled automatically. See\n" +
            "     * {@link #getSupportParentActivityIntent()} for how to specify the parent. If any activity\n" +
            "     * along the parent chain requires extra Intent arguments, the Activity subclass\n" +
            "     * should override the method {@link #onPrepareSupportNavigateUpTaskStack(androidx.core.app.TaskStackBuilder)}\n" +
            "     * to supply those arguments.</p>\n" +
            "     *\n" +
            "     * <p>See <a href=\"{@docRoot}guide/topics/fundamentals/tasks-and-back-stack.html\">Tasks and\n" +
            "     * Back Stack</a> from the developer guide and\n" +
            "     * <a href=\"{@docRoot}design/patterns/navigation.html\">Navigation</a> from the design guide\n" +
            "     * for more information about navigating within your app.</p>\n" +
            "     *\n" +
            "     * <p>See the {@link androidx.core.app.TaskStackBuilder} class and the Activity methods\n" +
            "     * {@link #getSupportParentActivityIntent()}, {@link #supportShouldUpRecreateTask(android.content.Intent)}, and\n" +
            "     * {@link #supportNavigateUpTo(android.content.Intent)} for help implementing custom Up navigation.</p>\n" +
            "     *\n" +
            "     * @return true if Up navigation completed successfully and this Activity was finished,\n" +
            "     *         false otherwise.\n" +
            "     */\n" +
            "    public boolean onSupportNavigateUp() {\n" +
            "        Intent upIntent = getSupportParentActivityIntent();\n" +
            "\n" +
            "        if (upIntent != null) {\n" +
            "            if (supportShouldUpRecreateTask(upIntent)) {\n" +
            "                TaskStackBuilder b = TaskStackBuilder.create(this);\n" +
            "                onCreateSupportNavigateUpTaskStack(b);\n" +
            "                onPrepareSupportNavigateUpTaskStack(b);\n" +
            "                b.startActivities();\n" +
            "\n" +
            "                try {\n" +
            "                    ActivityCompat.finishAffinity(this);\n" +
            "                } catch (IllegalStateException e) {\n" +
            "                    // This can only happen on 4.1+, when we don't have a parent or a result set.\n" +
            "                    // In that case we should just finish().\n" +
            "                    finish();\n" +
            "                }\n" +
            "            } else {\n" +
            "                // This activity is part of the application's task, so simply\n" +
            "                // navigate up to the hierarchical parent activity.\n" +
            "                supportNavigateUpTo(upIntent);\n" +
            "            }\n" +
            "            return true;\n" +
            "        }\n" +
            "        return false;\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Obtain an {@link android.content.Intent} that will launch an explicit target activity\n" +
            "     * specified by sourceActivity's {@link androidx.core.app.NavUtils#PARENT_ACTIVITY} &lt;meta-data&gt;\n" +
            "     * element in the application's manifest. If the device is running\n" +
            "     * Jellybean or newer, the android:parentActivityName attribute will be preferred\n" +
            "     * if it is present.\n" +
            "     *\n" +
            "     * @return a new Intent targeting the defined parent activity of sourceActivity\n" +
            "     */\n" +
            "    @Nullable\n" +
            "    @Override\n" +
            "    public Intent getSupportParentActivityIntent() {\n" +
            "        return NavUtils.getParentActivityIntent(this);\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Returns true if sourceActivity should recreate the task when navigating 'up'\n" +
            "     * by using targetIntent.\n" +
            "     *\n" +
            "     * <p>If this method returns false the app can trivially call\n" +
            "     * {@link #supportNavigateUpTo(android.content.Intent)} using the same parameters to correctly perform\n" +
            "     * up navigation. If this method returns false, the app should synthesize a new task stack\n" +
            "     * by using {@link androidx.core.app.TaskStackBuilder} or another similar mechanism to perform up navigation.</p>\n" +
            "     *\n" +
            "     * @param targetIntent An intent representing the target destination for up navigation\n" +
            "     * @return true if navigating up should recreate a new task stack, false if the same task\n" +
            "     *         should be used for the destination\n" +
            "     */\n" +
            "    public boolean supportShouldUpRecreateTask(@NonNull Intent targetIntent) {\n" +
            "        return NavUtils.shouldUpRecreateTask(this, targetIntent);\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Navigate from sourceActivity to the activity specified by upIntent, finishing sourceActivity\n" +
            "     * in the process. upIntent will have the flag {@link android.content.Intent#FLAG_ACTIVITY_CLEAR_TOP} set\n" +
            "     * by this method, along with any others required for proper up navigation as outlined\n" +
            "     * in the Android Design Guide.\n" +
            "     *\n" +
            "     * <p>This method should be used when performing up navigation from within the same task\n" +
            "     * as the destination. If up navigation should cross tasks in some cases, see\n" +
            "     * {@link #supportShouldUpRecreateTask(android.content.Intent)}.</p>\n" +
            "     *\n" +
            "     * @param upIntent An intent representing the target destination for up navigation\n" +
            "     */\n" +
            "    public void supportNavigateUpTo(@NonNull Intent upIntent) {\n" +
            "        NavUtils.navigateUpTo(this, upIntent);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void onContentChanged() {\n" +
            "        // Call onSupportContentChanged() for legacy reasons\n" +
            "        onSupportContentChanged();\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * @deprecated Use {@link #onContentChanged()} instead.\n" +
            "     */\n" +
            "    @Deprecated\n" +
            "    public void onSupportContentChanged() {\n" +
            "    }\n" +
            "\n" +
            "    @Nullable\n" +
            "    @Override\n" +
            "    public ActionBarDrawerToggle.Delegate getDrawerToggleDelegate() {\n" +
            "        return getDelegate().getDrawerToggleDelegate();\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * {@inheritDoc}\n" +
            "     *\n" +
            "     * <p>Please note: AppCompat uses its own feature id for the action bar:\n" +
            "     * {@link AppCompatDelegate#FEATURE_SUPPORT_ACTION_BAR FEATURE_SUPPORT_ACTION_BAR}.</p>\n" +
            "     */\n" +
            "    @Override\n" +
            "    public boolean onMenuOpened(int featureId, Menu menu) {\n" +
            "        return super.onMenuOpened(featureId, menu);\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * {@inheritDoc}\n" +
            "     *\n" +
            "     * <p>Please note: AppCompat uses its own feature id for the action bar:\n" +
            "     * {@link AppCompatDelegate#FEATURE_SUPPORT_ACTION_BAR FEATURE_SUPPORT_ACTION_BAR}.</p>\n" +
            "     */\n" +
            "    @Override\n" +
            "    public void onPanelClosed(int featureId, @NonNull Menu menu) {\n" +
            "        super.onPanelClosed(featureId, menu);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    protected void onSaveInstanceState(@NonNull Bundle outState) {\n" +
            "        super.onSaveInstanceState(outState);\n" +
            "        getDelegate().onSaveInstanceState(outState);\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * @return The {@link AppCompatDelegate} being used by this Activity.\n" +
            "     */\n" +
            "    @NonNull\n" +
            "    public AppCompatDelegate getDelegate() {\n" +
            "        if (mDelegate == null) {\n" +
            "            mDelegate = AppCompatDelegate.create(this, this);\n" +
            "        }\n" +
            "        return mDelegate;\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public boolean dispatchKeyEvent(KeyEvent event) {\n" +
            "        // Let support action bars open menus in response to the menu key prioritized over\n" +
            "        // the window handling it\n" +
            "        final int keyCode = event.getKeyCode();\n" +
            "        final ActionBar actionBar = getSupportActionBar();\n" +
            "        if (keyCode == KeyEvent.KEYCODE_MENU\n" +
            "                && actionBar != null && actionBar.onMenuKeyEvent(event)) {\n" +
            "            return true;\n" +
            "        }\n" +
            "        return super.dispatchKeyEvent(event);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public Resources getResources() {\n" +
            "        if (mResources == null && VectorEnabledTintResources.shouldBeUsed()) {\n" +
            "            mResources = new VectorEnabledTintResources(this, super.getResources());\n" +
            "        }\n" +
            "        return mResources == null ? super.getResources() : mResources;\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * KeyEvents with non-default modifiers are not dispatched to menu's performShortcut in API 25\n" +
            "     * or lower. Here, we check if the keypress corresponds to a menuitem's shortcut combination\n" +
            "     * and perform the corresponding action.\n" +
            "     */\n" +
            "    private boolean performMenuItemShortcut(int keycode, KeyEvent event) {\n" +
            "        if (!(Build.VERSION.SDK_INT >= 26) && !event.isCtrlPressed()\n" +
            "                && !KeyEvent.metaStateHasNoModifiers(event.getMetaState())\n" +
            "                && event.getRepeatCount() == 0\n" +
            "                && !KeyEvent.isModifierKey(event.getKeyCode())) {\n" +
            "            final Window currentWindow = getWindow();\n" +
            "            if (currentWindow != null && currentWindow.getDecorView() != null) {\n" +
            "                final View decorView = currentWindow.getDecorView();\n" +
            "                if (decorView.dispatchKeyShortcutEvent(event)) {\n" +
            "                    return true;\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "        return false;\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public boolean onKeyDown(int keyCode, KeyEvent event) {\n" +
            "        if (performMenuItemShortcut(keyCode, event)) {\n" +
            "            return true;\n" +
            "        }\n" +
            "        return super.onKeyDown(keyCode, event);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void openOptionsMenu() {\n" +
            "        ActionBar actionBar = getSupportActionBar();\n" +
            "        if (getWindow().hasFeature(Window.FEATURE_OPTIONS_PANEL)\n" +
            "                && (actionBar == null || !actionBar.openOptionsMenu())) {\n" +
            "            super.openOptionsMenu();\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void closeOptionsMenu() {\n" +
            "        ActionBar actionBar = getSupportActionBar();\n" +
            "        if (getWindow().hasFeature(Window.FEATURE_OPTIONS_PANEL)\n" +
            "                && (actionBar == null || !actionBar.closeOptionsMenu())) {\n" +
            "            super.closeOptionsMenu();\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Called when the night mode has changed. See {@link AppCompatDelegate#applyDayNight()} for\n" +
            "     * more information.\n" +
            "     *\n" +
            "     * @param mode the night mode which has been applied\n" +
            "     */\n" +
            "    protected void onNightModeChanged(@NightMode int mode) {\n" +
            "    }\n" +
            "}\n";

}
