package aa_voip;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import net.sf.fmj.media.RegistryDefaults;
import net.sf.fmj.ui.FmjStudio;
import net.sf.fmj.utility.ClasspathChecker;
import net.sf.fmj.utility.JmfUtility;
import net.sf.fmj.utility.LoggerSingleton;
import net.sf.fmj.utility.OSUtils;

public class Tester extends FmjStudio {

	private static final Logger logger = LoggerSingleton.logger;

	public static void main(String[] args) {
		try {

			System.setProperty("java.util.logging.config.file",
					"logging.properties");
			LogManager.getLogManager().readConfiguration();

			if (!ClasspathChecker.checkAndWarn()) {
				// JMF is ahead of us in the classpath. Let's do some things to
				// make this go more smoothly.
				logger.info("Enabling JMF logging");
				if (!JmfUtility.enableLogging())
					logger.warning("Failed to enable JMF logging");

				// Let's register our own prefixes, etc, since they won't
				// generally be if JMF is in charge.
				logger.info("Registering FMJ prefixes and plugins with JMF");
				RegistryDefaults.registerAll(RegistryDefaults.FMJ);
				// RegistryDefaults.unRegisterAll(RegistryDefaults.JMF); //
				// TODO: this can be used to make some things that work in FMJ
				// but not in JMF, work, like streaming mp3/ogg.
				// TODO: what about the removal of some/reordering?
			}

			// see http://developer.apple.com/technotes/tn/tn2031.html
			// see
			// http://java.sun.com/developer/technicalArticles/JavaLP/JavaToMac/
			// It doesn't seem to work to set these in code, they have to be set
			// by the calling environment
			if (OSUtils.isMacOSX()) {
				System.setProperty(
						"com.apple.mrj.application.apple.menu.about.name",
						"FMJ Studio");
				// System.setProperty("com.apple.mrj.application.growbox.intrudes",
				// "false"); // doesn't seem to work
			}

			//
			FmjStudio main = new FmjStudio();
			main.run(args);
		} catch (Throwable t) {
			logger.log(Level.WARNING, "" + t, t);
		}
	}

}
