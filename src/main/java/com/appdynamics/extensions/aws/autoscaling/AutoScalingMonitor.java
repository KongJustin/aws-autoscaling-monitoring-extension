package com.appdynamics.extensions.aws.autoscaling;

import static com.appdynamics.extensions.aws.Constants.METRIC_PATH_SEPARATOR;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.appdynamics.extensions.aws.SingleNamespaceCloudwatchMonitor;
import com.appdynamics.extensions.aws.collectors.NamespaceMetricStatisticsCollector;
import com.appdynamics.extensions.aws.config.Configuration;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;

/**
 * @author Florencio Sarmiento
 *
 */
public class AutoScalingMonitor extends SingleNamespaceCloudwatchMonitor<Configuration> {
	
	private static final Logger LOGGER = Logger.getLogger("com.singularity.extensions.aws.AutoScalingMonitor");

	private static final String DEFAULT_METRIC_PREFIX = String.format("%s%s%s%s", 
			"Custom Metrics", METRIC_PATH_SEPARATOR, "Amazon AutoScaling", METRIC_PATH_SEPARATOR);
	
	public AutoScalingMonitor() {
		super(Configuration.class);
		LOGGER.info(String.format("Using AWS AutoScaling Monitor Version [%s]", 
				this.getClass().getPackage().getImplementationTitle()));
	}

	@Override
	protected NamespaceMetricStatisticsCollector getNamespaceMetricsCollector(
			Configuration config) {
		MetricsProcessor metricsProcessor = createMetricsProcessor(config);

		return new NamespaceMetricStatisticsCollector
				.Builder(config.getAccounts(),
						config.getConcurrencyConfig(), 
						config.getMetricsConfig(),
						metricsProcessor)
				.withCredentialsEncryptionConfig(config.getCredentialsDecryptionConfig())
				.withProxyConfig(config.getProxyConfig())
				.build();
	}

	@Override
	protected Logger getLogger() {
		return LOGGER;
	}
	
	@Override
	protected String getMetricPrefix(Configuration config) {
		return StringUtils.isNotBlank(config.getMetricPrefix()) ? 
				config.getMetricPrefix() : DEFAULT_METRIC_PREFIX;
	}

	private MetricsProcessor createMetricsProcessor(Configuration config) {
		return new AutoScalingMetricsProcessor(
				config.getMetricsConfig().getMetricTypes(), 
				config.getMetricsConfig().getExcludeMetrics());
	}

}
