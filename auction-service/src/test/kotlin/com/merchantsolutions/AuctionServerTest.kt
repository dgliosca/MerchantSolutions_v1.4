package com.merchantsolutions

import com.merchantsolutions.SellerActor.Product
import com.merchantsolutions.adapters.InMemoryAuctions
import com.merchantsolutions.adapters.InMemoryUsers
import com.merchantsolutions.application.AuctionHub
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.Money.Companion.gbp
import com.merchantsolutions.domain.ProductId
import com.merchantsolutions.domain.UserId
import com.merchantsolutions.drivers.http.auctionApp
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasElement
import com.natpryce.hamkrest.isEmpty
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class AuctionServerTest {

    private val auctionServer = auctionApp(AuctionHub(testing, InMemoryUsers(), InMemoryAuctions()))
    private val buyerOne = BuyerActor(auctionServer)
    private val buyerOneAuthenticated = buyerOne.authenticated()

    private val buyerTwo = BuyerActor(auctionServer, "00000000-0000-0000-0000-000000000002")
    private val buyerTwoAuthenticated = buyerTwo.authenticated()

    private val sellerAuthenticated = SellerActor(auctionServer)
    private val backOffice = BackOfficeActor(auctionServer)
    private val userIdOne = UserId.of("00000000-0000-0000-0000-000000000001")

    @Test
    fun `seller can register a new product`() {
        val product = Product("Candle Sticks", Money(gbp, BigDecimal("12.13")))
        val productId = sellerAuthenticated.registerProduct(product)

        assertThat(productId, equalTo(ProductId.of("00000000-0000-0000-0000-000000000000")))
    }

    @Test
    fun `there are no auction to bid`() {
        assertThat(buyerOneAuthenticated.listAuctions(), equalTo(emptyList()))
    }

    @Test
    fun `backoffice list products to start selling`() {
        sellerAuthenticated.registerProduct(Product("Candle Sticks", Money(gbp, BigDecimal("12.13"))))
        val products = backOffice.listProducts()

        assertThat(products.map { it.description }, hasElement("Candle Sticks"))
    }

    @Test
    fun `there is one auction to bid`() {
        val productId = sellerAuthenticated.registerProduct(Product("Antique Vase", Money(gbp, BigDecimal("12.13"))))
        val auctionId = backOffice.createAuction(productId)
        backOffice.startAuction(auctionId)

        val auctionList = buyerOneAuthenticated.listAuctions()
        assertThat(auctionList, !isEmpty)
    }

    @Test
    fun `buyer can bid until auction closes`() {
        val productId = sellerAuthenticated.registerProduct(Product("Antique Vase", Money(gbp, BigDecimal("12.13"))))
        val auctionId = backOffice.createAuction(productId)
        backOffice.startAuction(auctionId)

        val response = buyerOneAuthenticated.placeABid(auctionId, Money(gbp, BigDecimal("12.13")))
        backOffice.closeAuction(auctionId)
        assertThat(response.status, equalTo(OK))
    }

    @Test
    fun `buyer place a bid and win`() {
        val productId = sellerAuthenticated.registerProduct(Product("Antique Vase", Money(gbp, BigDecimal("12.13"))))
        val auctionId = backOffice.createAuction(productId)
        backOffice.startAuction(auctionId)

        buyerOneAuthenticated.placeABid(auctionId, Money(gbp, BigDecimal("12.13")))
        backOffice.closeAuction(auctionId)

        assertThat(
            buyerOneAuthenticated.auctionResult(auctionId), equalTo(
                AuctionResult(
                    userIdOne,
                    Money(gbp, BigDecimal("12.13"))
                )
            )
        )
    }

    @Test
    fun `unauthorised buyer cannot bid`() {
        val productId = sellerAuthenticated.registerProduct(Product("Antique Vase", Money(gbp, BigDecimal("12.13"))))
        val auctionId = backOffice.createAuction(productId)
        backOffice.startAuction(auctionId)

        val response = buyerOne.placeABid(auctionId, Money(gbp, BigDecimal("12.13")))

        assertThat(response.status, equalTo(expected = UNAUTHORIZED))
    }

    @Test
    fun `bid gets ignored if below minimum seller price`() {
        val productId = sellerAuthenticated.registerProduct(Product("Antique Vase", Money(gbp, BigDecimal("10.00"))))
        val auctionId = backOffice.createAuction(productId)
        backOffice.startAuction(auctionId)

        val response = buyerOneAuthenticated.placeABid(auctionId, Money(gbp, BigDecimal("9.00")))

        assertThat(response.status, equalTo(CONFLICT))
    }

    @Test
    fun `bidder who first bid the highest price win`() {
        val productId = sellerAuthenticated.registerProduct(Product("Antique Vase", Money(gbp, BigDecimal("10.00"))))
        val auctionId = backOffice.createAuction(productId)
        backOffice.startAuction(auctionId)

        buyerOneAuthenticated.placeABid(auctionId, Money(gbp, BigDecimal("11.00")))
        buyerTwoAuthenticated.placeABid(auctionId, Money(gbp, BigDecimal("11.00")))

        backOffice.closeAuction(auctionId)

        assertThat(
            buyerOneAuthenticated.auctionResult(auctionId), equalTo(
                AuctionResult(
                    userIdOne,
                    Money(gbp, BigDecimal("11.00"))
                )
            )
        )
    }
}
