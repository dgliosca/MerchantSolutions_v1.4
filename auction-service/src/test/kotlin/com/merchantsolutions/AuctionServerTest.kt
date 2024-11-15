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
import org.junit.jupiter.api.fail
import java.math.BigDecimal
import java.util.UUID
import kotlin.lazy

class AuctionServerTest {

    private val buyer: BuyerActor by lazy { BuyerActor(auctionServer) }
    private val auctionServer: HttpHandler by lazy { auctionApp(AuctionHub(testing)) }
    private val seller = SellerActor(auctionServer)
    private val backOffice = BackOfficeActor(auctionServer)

    @Test
    fun `seller can register a new product`() {
        seller.registerProduct(SellerActor.Product("candle-sticks"))
    }

    @Test
    fun `there are no auction to bid`() {
        assertThat(buyer.listAuctions(), equalTo(emptyList()))
    }

    @Test
    fun `backoffice list products to start selling`() {
        seller.registerProduct(SellerActor.Product("Candle Sticks"))
        val products = backOffice.listProducts()

        assertThat(products.map { it.description }, hasElement("Candle Sticks"))
    }

    @Test
    fun `there is one auction to bid`() {
        seller.registerProduct(SellerActor.Product("Antique Vase"))
        val product = backOffice.listProducts()
            .find { it.description == "Antique Vase" } ?: fail("Couldn't find product")
        val auctionId = backOffice.createAuction(ProductId(product.id))
        backOffice.startAuction(auctionId)

        val auctionList = buyer.listAuctions()
        assertThat(auctionList, !isEmpty)
    }

    @Test
    fun `buyer can bid until auction closes`() {
        seller.registerProduct(SellerActor.Product("Antique Vase"))
        val product = backOffice.listProducts()
            .find { it.description == "Antique Vase" } ?: fail("Couldn't find product")
        val auctionId = backOffice.createAuction(ProductId(product.id))
        backOffice.startAuction(auctionId)

        val auction = buyer.listAuctions().first()
        buyer.placeABid(auction, Money(gbp, BigDecimal("12.13")))
        backOffice.closeAuction(product.id)
    }

    @Test
    fun `buyer lost auction`() {
        seller.registerProduct(SellerActor.Product("Antique Vase"))
        val product = backOffice.listProducts()
            .find { it.description == "Antique Vase" } ?: fail("Couldn't find product")
        val auctionId = backOffice.createAuction(ProductId(product.id))
        backOffice.startAuction(auctionId)

        val auction = buyer.listAuctions().first()
        buyer.placeABid(auction, Money(gbp, BigDecimal("12.13")))
        backOffice.closeAuction(product.id)

        assertThat(
            buyer.auctionResult(auction), equalTo(
                AuctionResult(
                    UserId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                    Money(gbp, BigDecimal("12.13"))
                )
            )
        )
    }

//    @Test
//    fun `buyer win auction`() {
//        seller.registerProduct(SellerActor.Product("Antique Vase"))
//        val product = backOffice.listProducts()
//            .find { it.description == "Antique Vase" } ?: fail("Couldn't find product")
//
//        val auctionId = backOffice.createAuction(ProductId(product.id))
//        backOffice.startAuction(auctionId)
//
//        val auction = buyer.listAuctions().first()
//        buyer.placeABid(auction, Money(gbp, BigDecimal("12.13")))
//        backOffice.closeAuction(product.id)
//
//        assertThat(buyer.auctionResult(auction).outcome, equalTo(AuctionOutcome.youLost))
//        assertThat(buyer.auctionResult(auction).winningBid, equalTo(Money(gbp, BigDecimal("12.13"))))
//    }
}
