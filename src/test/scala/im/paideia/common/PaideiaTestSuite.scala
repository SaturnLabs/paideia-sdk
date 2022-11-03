package im.paideia.common

import org.scalatest.funsuite.AnyFunSuite
import scala.util.Random
import im.paideia.HttpClientTesting
import im.paideia.DAOConfig
import im.paideia.Paideia
import im.paideia.util.Env
import im.paideia.DAO
import im.paideia.common.contracts.PaideiaContractSignature
import org.ergoplatform.appkit.NetworkType
import im.paideia.common.contracts.Config
import org.ergoplatform.appkit.impl.BlockchainContextImpl
import im.paideia.governance.contracts.PaideiaOrigin
import org.ergoplatform.appkit.ErgoId
import im.paideia.governance.contracts.ProtoDAOProxy
import im.paideia.common.contracts.Treasury
import im.paideia.governance.contracts.ProtoDAO
import im.paideia.governance.contracts.Mint
import im.paideia.util.ConfKeys
import im.paideia.governance.contracts.DAOOrigin
import im.paideia.common.contracts.OperatorIncentive

class PaideiaTestSuite extends AnyFunSuite with HttpClientTesting {
    
}

object PaideiaTestSuite {
    var initializedPaideia: Boolean = false

    def init(ctx: BlockchainContextImpl) = {
        if (!initializedPaideia) {
            val paideiaConfig = DAOConfig()
            paideiaConfig.set(ConfKeys.im_paideia_fees_createdao_erg,1000000000L)
            paideiaConfig.set(ConfKeys.im_paideia_fees_createdao_paideia,100L)
            paideiaConfig.set(ConfKeys.im_paideia_dao_key,ErgoId.create(Env.paideiaDaoKey).getBytes())
            Paideia.addDAO(DAO(Env.paideiaDaoKey,paideiaConfig))
            val configContract = Config(PaideiaContractSignature(daoKey = Env.paideiaDaoKey))
            val paideiaOriginContract = PaideiaOrigin(PaideiaContractSignature(daoKey = Env.paideiaDaoKey))
            val protoDaoProxyContract = ProtoDAOProxy(PaideiaContractSignature(daoKey = Env.paideiaDaoKey))
            val treasuryContract = Treasury(PaideiaContractSignature(daoKey = Env.paideiaDaoKey))
            val protoDAOContract = ProtoDAO(PaideiaContractSignature(daoKey = Env.paideiaDaoKey))
            val mintContract = Mint(PaideiaContractSignature(daoKey = Env.paideiaDaoKey))
            val daoContract = DAOOrigin(PaideiaContractSignature(daoKey = Env.paideiaDaoKey))
            val operatorIncentiveContract = OperatorIncentive(PaideiaContractSignature(daoKey = Env.paideiaDaoKey))
            paideiaConfig.set(ConfKeys.im_paideia_contracts_treasury,treasuryContract.contractSignature)
            paideiaConfig.set(ConfKeys.im_paideia_contracts_protodao,protoDAOContract.contractSignature)
            paideiaConfig.set(ConfKeys.im_paideia_contracts_protodaoproxy,protoDaoProxyContract.contractSignature)
            paideiaConfig.set(ConfKeys.im_paideia_contracts_mint,mintContract.contractSignature)
            paideiaConfig.set(ConfKeys.im_paideia_contracts_config,configContract.contractSignature)
            paideiaConfig.set(ConfKeys.im_paideia_contracts_dao,daoContract.contractSignature)
            paideiaConfig.set(ConfKeys.im_paideia_contracts_operatorincentive,operatorIncentiveContract.contractSignature)
            configContract.newBox(configContract.box(ctx,Paideia.getDAO(Env.paideiaDaoKey)).inputBox(),false)
            paideiaOriginContract.newBox(paideiaOriginContract.box(ctx,paideiaConfig,1000000L).inputBox(),false)
            operatorIncentiveContract.newBox(operatorIncentiveContract.box(ctx,100000000000L,1000000000L).inputBox(),false)
            initializedPaideia = true
        }
    }
}