package com.eugenedondo.leftrightdrawer.lrdlibrary

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.design.widget.NavigationView
import android.support.v4.graphics.ColorUtils
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import java.util.*

open class LeftRightDrawerLayout : DrawerLayout {

    internal var settings = HashMap<Int, Setting>()
    private var defaultScrimColor = -0x67000000
    private var defaultDrawerElevation: Float = 0.toFloat()
    private var frameLayout: FrameLayout? = null
    var drawerView: View? = null
    private var statusBarColor: Int = 0
    private var defaultFitsSystemWindows: Boolean = false
    private var contrastThreshold = 3f

    internal val activity: Activity?
        get() = getActivity(context)

    constructor(context: Context) : super(context) {
        init(context, null, 0)

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)

    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs, defStyle)

    }


    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        defaultDrawerElevation = drawerElevation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            defaultFitsSystemWindows = fitsSystemWindows
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusBarColor = activity!!.window.statusBarColor
        }
        addDrawerListener(object : DrawerLayout.DrawerListener {

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                this@LeftRightDrawerLayout.drawerView = drawerView
                updateSlideOffset(drawerView, slideOffset)
            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerStateChanged(newState: Int) {

            }
        })

        frameLayout = FrameLayout(context)
        frameLayout!!.setPadding(0, 0, 0, 0)
        super.addView(frameLayout)

    }


    //    @Override
    //    public void setFitsSystemWindows(boolean fitSystemWindows) {
    ////        defaultFitsSystemWindows = fitSystemWindows;
    //
    //        super.setFitsSystemWindows(fitSystemWindows);
    //
    //        if (ViewCompat.getFitsSystemWindows(this)) {
    //            if (Build.VERSION.SDK_INT >= 21) {
    //                this.setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener() {
    //                    @SuppressLint("RestrictedApi")
    //                    public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
    //                        DrawerLayout drawerLayout = (DrawerLayout)view;
    //                        drawerLayout.setChildInsets(insets, insets.getSystemWindowInsetTop() > 0);
    //                        return insets.consumeSystemWindowInsets();
    //                    }
    //                });
    //                this.setSystemUiVisibility(1280);
    //                TypedArray a = getC.obtainStyledAttributes(THEME_ATTRS);
    //
    //                try {
    //                    this.mStatusBarBackground = a.getDrawable(0);
    //                } finally {
    //                    a.recycle();
    //                }
    //            } else {
    //                this.mStatusBarBackground = null;
    //            }
    //        }
    //    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        child.layoutParams = params
        addView(child)
    }

    override fun addView(child: View) {
        if (child is NavigationView) {
            super.addView(child)
        } else {
            val cardView = CardView(context)
            cardView.radius = 0f
            cardView.addView(child)
            cardView.cardElevation = 0f
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                cardView.setContentPadding(-6, -9, -6, -9)
            }
            frameLayout!!.addView(cardView)
        }
    }

    fun setViewScale(gravity: Int, percentage: Float) {
        val absGravity = getDrawerViewAbsoluteGravity(gravity)
        val setting: Setting?
        if (!settings.containsKey(absGravity)) {
            setting = createSetting()
            settings[absGravity] = setting
        } else
            setting = settings[absGravity]

        assert(setting != null)
        setting!!.percentage = percentage
        if (percentage < 1) {
            setStatusBarBackground(null)
            systemUiVisibility = 0
        }

        setting.scrimColor = Color.TRANSPARENT
        setting.drawerElevation = 0f
    }

    fun setViewElevation(gravity: Int, elevation: Float) {
        val absGravity = getDrawerViewAbsoluteGravity(gravity)
        val setting: Setting?
        if (!settings.containsKey(absGravity)) {
            setting = createSetting()
            settings[absGravity] = setting
        } else
            setting = settings[absGravity]

        assert(setting != null)
        setting!!.scrimColor = Color.TRANSPARENT
        setting.drawerElevation = 0f
        setting.elevation = elevation
    }

    fun setViewScrimColor(gravity: Int, scrimColor: Int) {
        val absGravity = getDrawerViewAbsoluteGravity(gravity)
        val setting: Setting?
        if (!settings.containsKey(absGravity)) {
            setting = createSetting()
            settings[absGravity] = setting
        } else
            setting = settings[absGravity]

        assert(setting != null)
        setting!!.scrimColor = scrimColor
    }

    fun setDrawerElevation(gravity: Int, elevation: Float) {
        val absGravity = getDrawerViewAbsoluteGravity(gravity)
        val setting: Setting?
        if (!settings.containsKey(absGravity)) {
            setting = createSetting()
            settings[absGravity] = setting
        } else
            setting = settings[absGravity]

        assert(setting != null)
        setting!!.elevation = 0f
        setting.drawerElevation = elevation
    }


    fun setRadius(gravity: Int, radius: Float) {
        val absGravity = getDrawerViewAbsoluteGravity(gravity)
        val setting: Setting?
        if (!settings.containsKey(absGravity)) {
            setting = createSetting()
            settings[absGravity] = setting
        } else
            setting = settings[absGravity]

        setting!!.radius = radius
    }


    fun getSetting(gravity: Int): Setting? {
        val absGravity = getDrawerViewAbsoluteGravity(gravity)
        return settings[absGravity]

    }

    override fun setDrawerElevation(elevation: Float) {
        defaultDrawerElevation = elevation
        super.setDrawerElevation(elevation)
    }

    override fun setScrimColor(@ColorInt color: Int) {
        defaultScrimColor = color
        super.setScrimColor(color)
    }

    fun useCustomBehavior(gravity: Int) {
        val absGravity = getDrawerViewAbsoluteGravity(gravity)

        if (!settings.containsKey(absGravity)) {
            val setting = createSetting()
            settings[absGravity] = setting
        }
    }

    fun removeCustomBehavior(gravity: Int) {
        val absGravity = getDrawerViewAbsoluteGravity(gravity)

        if (settings.containsKey(absGravity)) {
            settings.remove(absGravity)
        }
    }


    override fun openDrawer(drawerView: View, animate: Boolean) {
        super.openDrawer(drawerView, animate)

        post { updateSlideOffset(drawerView, if (isDrawerOpen(drawerView)) 1f else 0f) }
    }

    private fun updateSlideOffset(drawerView: View, slideOffset: Float) {
        val absHorizGravity = getDrawerViewAbsoluteGravity(Gravity.START)
        val childAbsGravity = getDrawerViewAbsoluteGravity(drawerView)

        val activity = activity
        val window = activity!!.window


        var isRtl = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            isRtl = layoutDirection == View.LAYOUT_DIRECTION_RTL ||
                    window.decorView.layoutDirection == View.LAYOUT_DIRECTION_RTL ||
                    resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
        }

        for (i in 0 until frameLayout!!.childCount) {

            val child = frameLayout!!.getChildAt(i) as CardView
            val setting = settings[childAbsGravity]
            val adjust: Float

            if (setting != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && setting.percentage < 1.0) {

                    if (drawerView.background is ColorDrawable) {

                        val color = ColorUtils.setAlphaComponent(statusBarColor, (255 - 255 * slideOffset).toInt())
                        window.statusBarColor = color

                        val bgColor = (drawerView.background as ColorDrawable).color
                        window.decorView.setBackgroundColor(bgColor)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            systemUiVisibility = if (ColorUtils.calculateContrast(
                                    Color.WHITE,
                                    bgColor
                                ) < contrastThreshold && slideOffset > 0.4
                            ) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
                        }
                    }


                }

                child.radius = (setting.radius * slideOffset).toInt().toFloat()
                super.setScrimColor(setting.scrimColor)
                super.setDrawerElevation(setting.drawerElevation)
                val percentage = 1f - setting.percentage
                ViewCompat.setScaleY(child, 1f - percentage * slideOffset)
                child.cardElevation = setting.elevation * slideOffset
                adjust = setting.elevation
                val isLeftDrawer: Boolean
                if (isRtl)
                    isLeftDrawer = childAbsGravity != absHorizGravity
                else
                    isLeftDrawer = childAbsGravity == absHorizGravity
                val width = if (isLeftDrawer)
                    drawerView.width + adjust
                else
                    -drawerView.width - adjust
                updateSlideOffset(child, setting, width, slideOffset, isLeftDrawer)

            } else {
                super.setScrimColor(defaultScrimColor)
                super.setDrawerElevation(defaultDrawerElevation)
            }


        }

    }

    fun setContrastThreshold(contrastThreshold: Float) {
        this.contrastThreshold = contrastThreshold
    }

    internal fun getActivity(context: Context?): Activity? {
        if (context == null) return null
        if (context is Activity) return context
        return if (context is ContextWrapper) getActivity(context.baseContext) else null
    }

    internal open fun updateSlideOffset(
        child: CardView,
        setting: Setting?,
        width: Float,
        slideOffset: Float,
        isLeftDrawer: Boolean
    ) {
        ViewCompat.setX(child, width * slideOffset)
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (drawerView != null)
            updateSlideOffset(drawerView!!, if (isDrawerOpen(drawerView!!)) 1f else 0f)
    }


    internal fun getDrawerViewAbsoluteGravity(gravity: Int): Int {

        return GravityCompat.getAbsoluteGravity(
            gravity,
            ViewCompat.getLayoutDirection(this)
        ) and Gravity.HORIZONTAL_GRAVITY_MASK

    }

    internal fun getDrawerViewAbsoluteGravity(drawerView: View): Int {
        val gravity = (drawerView.layoutParams as DrawerLayout.LayoutParams).gravity
        return getDrawerViewAbsoluteGravity(gravity)
    }


    internal open fun createSetting(): Setting {
        return Setting()
    }

    open inner class Setting {
        var fitsSystemWindows: Boolean = false
        var percentage = 1f
        var scrimColor = defaultScrimColor
        var elevation = 0f
        var drawerElevation = defaultDrawerElevation
        var radius: Float = 0.toFloat()
    }
}
