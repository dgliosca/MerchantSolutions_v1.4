package com.merchantsolutions

import com.merchantsolutions.application.AuctionHub
import com.merchantsolutions.domain.Money
import com.merchantsolutions.domain.Money.Companion.gbp
import com.merchantsolutions.domain.ProductId
import com.merchantsolutions.domain.UserId
import com.merchantsolutions.drivers.http.auctionApp
import org.http4k.core.*
import org.junit.jupiter.api.Test
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasElement
import com.natpryce.hamkrest.isEmpty
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.junit.jupiter.api.fail
import java.math.BigDecimal
import java.util.UUID
import kotlin.lazy

class AuctionServerTest {

    private val buyerOne: BuyerActor by lazy { BuyerActor(auctionServer) }
    private val buyerTwo: BuyerActor by lazy { BuyerActor(auctionServer, "00000000-0000-0000-0000-000000000002") }
    private val auctionServer: HttpHandler by lazy { auctionApp(AuctionHub(testing)) }
    private val seller = SellerActor(auctionServer)
    private val backOffice = BackOfficeActor(auctionServer)

    @Test
    fun `seller can register a new product`() {
        seller.registerProduct(SellerActor.Product("candle-sticks", Money(gbp, BigDecimal("12.13"))))
    }

    @Test
    fun `there are no auction to bid`() {
        assertThat(buyerOne.authenticated().listAuctions(), equalTo(emptyList()))
    }

    @Test
    fun `backoffice list products to start selling`() {
        seller.registerProduct(SellerActor.Product("Candle Sticks", Money(gbp, BigDecimal("12.13"))))
        val products = backOffice.listProducts()

        assertThat(products.map { it.description }, hasElement("Candle Sticks"))
    }

    @Test
    fun `there is one auction to bid`() {
        val productId = seller.registerProduct(SellerActor.Product("Antique Vase", Money(gbp, BigDecimal("12.13"))))
        val auctionId = backOffice.createAuction(productId)
        backOffice.startAuction(auctionId)

        val auctionList = buyerOne.authenticated().listAuctions()
        assertThat(auctionList, !isEmpty)
    }

    @Test
    fun `buyer can bid until auction closes`() {
        val productId = seller.registerProduct(SellerActor.Product("Antique Vase", Money(gbp, BigDecimal("12.13"))))
        val auctionId = backOffice.createAuction(productId)
        backOffice.startAuction(auctionId)

        val auction = buyerOne.authenticated().listAuctions().first()
        buyerOne.placeABid(auction.auctionId, Money(gbp, BigDecimal("12.13")))
        backOffice.closeAuction(productId)
    }

    @Test
    fun `buyer place a bid and win`() {
        val productId = seller.registerProduct(SellerActor.Product("Antique Vase", Money(gbp, BigDecimal("12.13"))))
        val auctionId = backOffice.createAuction(productId)
        backOffice.startAuction(auctionId)

        val buyer = buyerOne.authenticated()
        val auction = buyer.listAuctions().first()
        buyer.placeABid(auction.auctionId, Money(gbp, BigDecimal("12.13")))
        backOffice.closeAuction(productId)

        assertThat(
            buyer.auctionResult(auction.auctionId), equalTo(
                AuctionResult(
                    UserId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                    Money(gbp, BigDecimal("12.13"))
                )
            )
        )
    }

    @Test
    fun `unauthorised buyer cannot bid`() {
        val productId = seller.registerProduct(SellerActor.Product("Antique Vase", Money(gbp, BigDecimal("12.13"))))
        val auctionId = backOffice.createAuction(productId)
        backOffice.startAuction(auctionId)

        val response = buyerOne.notAuthenticated().placeABid(auctionId, Money(gbp, BigDecimal("12.13")))

        assertThat(response.status, equalTo(expected = UNAUTHORIZED))
    }

    @Test
    fun `bid gets ignored if below minimum seller price`() {
        val productId = seller.registerProduct(SellerActor.Product("Antique Vase", Money(gbp, BigDecimal("10.00"))))
        val auctionId = backOffice.createAuction(productId)
        backOffice.startAuction(auctionId)

        val authenticatedBuyer = buyerOne.authenticated()
        val response = authenticatedBuyer.placeABid(auctionId, Money(gbp, BigDecimal("9.00")))

        assertThat(response.status, equalTo(CONFLICT))
    }

    @Test
    fun `bidder who first bid the highest price win`() {
        val productId = seller.registerProduct(SellerActor.Product("Antique Vase", Money(gbp, BigDecimal("10.00"))))
        val auctionId = backOffice.createAuction(productId)
        backOffice.startAuction(auctionId)

        val authenticatedBuyerOne = buyerOne.authenticated()
        val authenticatedBuyerTwo = buyerTwo.authenticated()
        val auction = authenticatedBuyerOne.listAuctions().first()

        authenticatedBuyerOne.placeABid(auctionId, Money(gbp, BigDecimal("11.00")))
        authenticatedBuyerTwo.placeABid(auctionId, Money(gbp, BigDecimal("11.00")))

        backOffice.closeAuction(productId)

        assertThat(
            authenticatedBuyerOne.auctionResult(auction.auctionId), equalTo(
                AuctionResult(
                    UserId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                    Money(gbp, BigDecimal("11.00"))
                )
            )
        )
    }
}
