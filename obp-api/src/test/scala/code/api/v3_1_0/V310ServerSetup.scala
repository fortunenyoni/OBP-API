package code.api.v3_1_0

import code.api.util.APIUtil.OAuth.{Consumer, Token, _}
import code.api.v1_2_1._
import code.api.v2_0_0.BasicAccountsJSON
import code.api.v3_0_0.{TransactionJsonV300, TransactionsJsonV300, ViewsJsonV300}
import code.setup.{APIResponse, DefaultUsers, ServerSetupWithTestData}
import dispatch.Req

import scala.util.Random.nextInt

trait V310ServerSetup extends ServerSetupWithTestData with DefaultUsers {

  def v3_1_0_Request: Req = baseRequest / "obp" / "v3.1.0"

  //When new version, this would be the first endpoint to test, to make sure it works well. 
  def getAPIInfo : APIResponse = {
    val request = v3_1_0_Request
    makeGetRequest(request)
    
  }

  def randomBankId : String = {
    def getBanksInfo : APIResponse  = {
      val request = v3_1_0_Request / "banks"
      makeGetRequest(request)
    }
    val banksJson = getBanksInfo.body.extract[BanksJSON]
    val randomPosition = nextInt(banksJson.banks.size)
    val bank = banksJson.banks(randomPosition)
    bank.id
  }
  def getPrivateAccounts(bankId : String, consumerAndToken: Option[(Consumer, Token)]) : APIResponse = {
    val request = v3_1_0_Request / "banks" / bankId / "accounts" / "private" <@(consumerAndToken)
    makeGetRequest(request)
  }

  def randomPrivateAccountId(bankId : String) : String = {
    val accountsJson = getPrivateAccounts(bankId, user1).body.extract[BasicAccountsJSON].accounts
    val randomPosition = nextInt(accountsJson.size)
    accountsJson(randomPosition).id
  }

  def randomPrivateAccount(bankId : String): AccountJSON = {
    val accountsJson = getPrivateAccounts(bankId, user1).body.extract[AccountsJSON].accounts
    val randomPosition = nextInt(accountsJson.size)
    accountsJson(randomPosition)
  }

  def randomViewPermalink(bankId: String, account: AccountJSON) : String = {
    val request = v3_1_0_Request / "banks" / bankId / "accounts" / account.id / "views" <@(consumer, token1)
    val reply = makeGetRequest(request)
    val possibleViewsPermalinks = reply.body.extract[ViewsJsonV300].views.filterNot(_.is_public==true)
    val randomPosition = nextInt(possibleViewsPermalinks.size)
    possibleViewsPermalinks(randomPosition).id
  }

  def getTransactions(bankId : String, accountId : String, viewId : String, consumerAndToken: Option[(Consumer, Token)], params: List[(String, String)] = Nil): APIResponse = {
    val request = v3_1_0_Request / "banks" / bankId / "accounts" / accountId / viewId / "transactions" <@(consumerAndToken)
    makeGetRequest(request, params)
  }

  def randomTransaction(bankId : String, accountId : String, viewId: String) : TransactionJsonV300 = {
    val transactionsJson = getTransactions(bankId, accountId, viewId, user1).body.extract[TransactionsJsonV300].transactions
    val randomPosition = nextInt(transactionsJson.size)
    transactionsJson(randomPosition)
  }


  
}