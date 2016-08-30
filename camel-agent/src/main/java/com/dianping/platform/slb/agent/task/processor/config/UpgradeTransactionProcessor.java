package com.dianping.platform.slb.agent.task.processor.config;

import com.dianping.platform.slb.agent.conf.ConfigureManager;
import com.dianping.platform.slb.agent.task.processor.AbstractTransactionProcessor;
import com.dianping.platform.slb.agent.task.workflow.engine.Engine;
import com.dianping.platform.slb.agent.task.workflow.step.ConfigUpgradeStep;
import com.dianping.platform.slb.agent.task.workflow.step.Step;
import com.dianping.platform.slb.agent.transaction.Transaction;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * dianping.com @2015
 * slb - soft load balance
 * <p/>
 * Created by leon.li(Li Yang)
 */
@Service
public class UpgradeTransactionProcessor extends AbstractTransactionProcessor {

	private final static Logger m_logger = Logger.getLogger(UpgradeTransactionProcessor.class);

	@Autowired
	private Engine m_engine;

	@Override
	protected Transaction.Status doTransaction(Transaction transaction) {
		transaction.addProperty(ConfigureManager.PROPERTY_TRANSACTIONMANAGER, m_transactionManager);
		int exitCode = Step.CODE_FAIL;

		try {
			exitCode = m_engine.executeStep(ConfigUpgradeStep.INIT, transaction);
		} catch (Exception ex) {
			exitCode = Step.CODE_FAIL;
			m_logger.error("[do transaction error]" + transaction.getTransactionID(), ex);
		}
		if (exitCode == Step.CODE_SUCCESS) {
			return Transaction.Status.SUCCESS;
		} else {
			return Transaction.Status.FAILED;
		}
	}

	@Override
	public boolean cancel(long txId) {
		if (isTransactionProcessing(txId)) {
			m_engine.kill();
			return true;
		}
		return false;
	}

}
