package com.merchantsolutions

import com.merchantsolutions.SellerActor.Product
import com.merchantsolutions.adapters.InMemoryAuctions
import com.merchantsolutions.adapters.InMemoryProducts
import com.merchantsolutions.adapters.users.InMemoryUsers
import com.merchantsolutions.application.AuctionHub
import com.merchantsolutions.domain.AuctionId
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
import java.util.UUID
import com.merchantsolutions.AuctionJson.json
import com.merchantsolutions.adapters.db.H2DB
import com.merchantsolutions.adapters.db.Storage
import com.natpryce.hamkrest.hasSize
import org.http4k.core.Status.Companion.NOT_FOUND

class AuctionServerTest {
    private val auctionHub = AuctionHub(InMemoryUsers(), InMemoryAuctions(testing), InMemoryProducts(testing))
    private val auctionServer = auctionApp(auctionHub)

    private val buyerOneId = UserId.of("00000000-0000-0000-0000-000000000001")
    private val buyerOne = BuyerActor(auctionServer, "00000000-0000-0000-0000-000000000001")

    private val buyerOneAuthenticated = buyerOne.authenticated()
    private val buyerTwo = BuyerActor(auctionServer, "00000000-0000-0000-0000-000000000002")

    private val buyerTwoAuthenticated = buyerTwo.authenticated()
    private val sellerAuthenticated = SellerActor(auctionServer)
    private val backOffice = BackOfficeActor(auctionServer)

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
    fun `list the auctions in which it is possible to bid`() {
        val productOne = sellerAuthenticated.registerProduct(Product("Candle Sticks", Money(gbp, BigDecimal("12.13"))))
        val productTwo = sellerAuthenticated.registerProduct(Product("Antique Vase", Money(gbp, BigDecimal("1.13"))))
        val productThree = sellerAuthenticated.registerProduct(Product("Napolean Chair", Money(gbp, BigDecimal("10.13"))))

        backOffice.createAuction(productOne)
        openAuctionFor(productTwo)
        openAuctionFor(productThree)

        val auctionList = buyerOneAuthenticated.listAuctions()
        assertThat(auctionList, hasSize(equalTo(2)))
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
        openAuctionFor(productId)

        val auctionList = buyerOneAuthenticated.listAuctions()
        assertThat(auctionList, !isEmpty)
    }

    private fun openAuctionFor(productTwo: ProductId): AuctionId {
        val auctionTwo = backOffice.createAuction(productTwo)
        backOffice.startAuction(auctionTwo)
        return auctionTwo
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

        val response = buyerOneAuthenticated.auctionResult(auctionId)
        assertThat(response.json<AuctionClosed>(), equalTo(
                AuctionClosed(
                    buyerOneId,
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

        val actual = buyerOneAuthenticated.auctionResult(auctionId)
        assertThat(
            actual.json<AuctionClosed>(), equalTo(
                AuctionClosed(
                    buyerOneId,
                    Money(gbp, BigDecimal("11.00"))
                )
            )
        )
    }

    @Test
    fun `auction not found`() {
        val auctionId = AuctionId(UUID.fromString("00000000-0001-0001-0001-000000000000"))
        val actual = buyerOneAuthenticated.auctionResult(auctionId)
        assertThat(actual.status, equalTo(NOT_FOUND))
    }

    @Test
    fun `buyer offer get rejected if below minimum price`() {
        val productId = sellerAuthenticated.registerProduct(Product("Antique Vase", Money(gbp, BigDecimal("10.00"))))
        val auctionId = backOffice.createAuction(productId)
        backOffice.startAuction(auctionId)

        val response = buyerOneAuthenticated.placeABid(auctionId, Money(gbp, BigDecimal("9.00")))

        assertThat(response.status, equalTo(CONFLICT))
    }

    @Test
    fun `buyer cannot offer a subsequent bid lower than the last one`() {
        val productId = sellerAuthenticated.registerProduct(Product("Antique Vase", Money(gbp, BigDecimal("10.00"))))
        val auctionId = backOffice.createAuction(productId)
        backOffice.startAuction(auctionId)

        buyerOneAuthenticated.placeABid(auctionId, Money(gbp, BigDecimal("12.00")))
        buyerOneAuthenticated.placeABid(auctionId, Money(gbp, BigDecimal("11.00")))

        backOffice.closeAuction(auctionId)

        val actual = buyerOneAuthenticated.auctionResult(auctionId)
        assertThat(
            actual.json<AuctionClosed>(), equalTo(
                AuctionClosed(
                    buyerOneId,
                    Money(gbp, BigDecimal("12.00"))
                )
            )
        )
    }

    @Test
    fun `highest bidder win`() {
        val productId = sellerAuthenticated.registerProduct(Product("Antique Vase", Money(gbp, BigDecimal("10.00"))))
        val auctionId = backOffice.createAuction(productId)
        backOffice.startAuction(auctionId)

        buyerOneAuthenticated.placeABid(auctionId, Money(gbp, BigDecimal("12.00")))
        buyerTwoAuthenticated.placeABid(auctionId, Money(gbp, BigDecimal("11.00")))

        backOffice.closeAuction(auctionId)

        val actual = buyerOneAuthenticated.auctionResult(auctionId)
        assertThat(
            actual.json<AuctionClosed>(), equalTo(
                AuctionClosed(
                    buyerOneId,
                    Money(gbp, BigDecimal("12.00"))
                )
            )
        )
    }
}
