/* 
 * This file is part of OppiaMobile - http://oppia-mobile.org/
 * 
 * OppiaMobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OppiaMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OppiaMobile. If not, see <http://www.gnu.org/licenses/>.
 */

package org.digitalcampus.mobile.learning.application;

import java.io.File;

import org.digitalcampus.mobile.learning.task.SubmitTrackerTask;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

public class MobileLearning extends Application {

	public static final String TAG = MobileLearning.class.getSimpleName();

	// local storage vars
	public static final String OPPIAMOBILE_ROOT = Environment
			.getExternalStorageDirectory() + "/digitalcampus/";
	public static final String MODULES_PATH = OPPIAMOBILE_ROOT + "modules/";
	public static final String MEDIA_PATH = OPPIAMOBILE_ROOT + "media/";
	public static final String DOWNLOAD_PATH = OPPIAMOBILE_ROOT + "download/";
	public static final String MODULE_XML = "module.xml";
	public static final String MODULE_SCHEDULE_XML = "schedule.xml";
	public static final String MODULE_TRACKER_XML = "tracker.xml";
	// server path vars
	
	public static final String LOGIN_PATH = "api/v1/user/";
	public static final String REGISTER_PATH = "api/v1/register/";
	public static final String MQUIZ_SUBMIT_PATH = "api/v1/quizattempt/";
	
	public static final String SERVER_MODULES_PATH = "modules/api/v1/module/";
	public static final String TRACKER_PATH = "modules/api/v1/tracker/";
	
	public static final String SERVER_POINTS_PATH = "badges/api/v1/points/";
	
	// general other settings
	public static final String BUGSENSE_API_KEY = "84d61fd0";
	public static final int PASSWORD_MIN_LENGTH = 6;
	public static final int PAGE_READ_TIME = 3;
	public static final String USER_AGENT = "OppiaMobile Android: ";
	public static final int MQUIZ_PASS_THRESHOLD = 100;
	public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");
	public static final DateTimeFormatter TIME_FORMAT = DateTimeFormat.forPattern("HH:mm:ss");
	
	// only used in case a module doesn't have any lang specified
	public static final String DEFAULT_LANG = "en";
	
	// for tracking if SubmitTrackerTask is already running
	public SubmitTrackerTask omSubmitTrackerTask = null;
	
	public static boolean createDirs() {
		String cardstatus = Environment.getExternalStorageState();
		if (cardstatus.equals(Environment.MEDIA_REMOVED)
				|| cardstatus.equals(Environment.MEDIA_UNMOUNTABLE)
				|| cardstatus.equals(Environment.MEDIA_UNMOUNTED)
				|| cardstatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY)
				|| cardstatus.equals(Environment.MEDIA_SHARED)) {
			return false;
		}

		String[] dirs = { OPPIAMOBILE_ROOT, MODULES_PATH, MEDIA_PATH, DOWNLOAD_PATH };

		for (String dirName : dirs) {
			File dir = new File(dirName);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					return false;
				}
			} else {
				if (!dir.isDirectory()) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean isLoggedIn(Activity act) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act.getBaseContext());
		String username = prefs.getString("prefUsername", "");
		String apiKey = prefs.getString("prefApiKey", "");
		if (username.trim().equals("") || apiKey.trim().equals("")) {
			return false;
		} else {
			return true;
		}
	}

}
