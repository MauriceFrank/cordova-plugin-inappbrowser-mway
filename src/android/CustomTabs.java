package org.apache.cordova.inappbrowser;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;

import java.util.ArrayList;
import java.util.List;

import static androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION;

public class CustomTabs {
    /**
     * Reference for the Google Chrome application package
     */
    private static final String CHROME_PACKAGE_NAME = "com.android.chrome";

    /**
     * Checks all available application packages, which are able to handle view intents. If Google Chrome is installed, this application will be used as prefered option.
     * All customaziable UI Elements get set within this function.
     * @param context
     * @param url
     * @param toolbarColor
     * @param closeButton
     * @param showTitle
     * @param hideUrlBar
     * @param shareState
     * @return
     */
    public static boolean openWebBrowser(Context context, String url, int toolbarColor, Bitmap closeButton, boolean showTitle, boolean hideUrlBar, int shareState) {
      List<String> packageNames = getCustomTabsPackages(context);

      if (packageNames.size() > 0) {
          CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
          builder.setToolbarColor(toolbarColor);
          builder.setUrlBarHidingEnabled(hideUrlBar);
          builder.setShareState(shareState);
          builder.setShowTitle(showTitle);
          if (closeButton != null) {
              builder.setCloseButtonIcon(closeButton);
          }
          Intent intent = new Intent(Intent.ACTION_SEND);
          intent.setType("text/plain");
          intent.putExtra(Intent.EXTRA_TEXT, url);
          CustomTabsIntent customTabsIntent = builder.build();
          if (packageNames.contains(CHROME_PACKAGE_NAME)) {
              customTabsIntent.intent.setPackage(CHROME_PACKAGE_NAME);
          }
          customTabsIntent.launchUrl(context, Uri.parse(url));
          return true;
      }
      return false;
    }

    /**
     * Returns a list of packages that support handling of view intents.
     */
    private static List<String> getCustomTabsPackages(Context context) {

        PackageManager pm = context.getPackageManager();
        // Get default VIEW intent handler.
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"));

        // Get all apps that can handle VIEW intents.
        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        List<String> packagesSupportingCustomTabs = new ArrayList<>();
        for (ResolveInfo info : resolvedActivityList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction(ACTION_CUSTOM_TABS_CONNECTION);
            String packageName = info.activityInfo.packageName;
            serviceIntent.setPackage(packageName);
            // Check if this package also resolves the Custom Tabs service.
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(packageName);
            }
        }
        return packagesSupportingCustomTabs;
    }

}
