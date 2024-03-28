package com.jinvicky.part10

import kotlin.random.Random

enum class Hand(private val handName: String, private val handValue: Int) {
    ROCK("바위", 0),
    SCISSORS("가위", 1),
    PAPER("보", 2);

    /**
     * 원래 자바 코드, 코틀린은 static이 없음.
     * public static Hand getHand(int handValue) {
     *   return hands[handValue];
     * }
     */
    companion object {
        private val hands: Array<Hand> = values()

        fun getHand(handValue: Int): Hand = hands[handValue]
    }

    fun isStrongerThan(h: Hand): Boolean = fight(h) == 1
    fun isWeakerThan(h: Hand): Boolean = fight(h) == -1

    private fun fight(h: Hand): Int {
        return when {
            this == h -> 0
            (this.handValue + 1) % 3 == h.handValue -> 1
            else -> -1
        }
    }

    override fun toString(): String = handName
}

interface Strategy {
    fun nextHand(): Hand
    fun study(win: Boolean)
}

class WinningStrategy(seed: Int) : Strategy  {
    var random: Random = Random(seed)
    var won: Boolean = false
    var prevHand: Hand? = null

    override fun nextHand(): Hand {
        if (prevHand == null || !won) {
            prevHand = Hand.getHand(random.nextInt(3))
        }
        return prevHand!!
    }
    override fun study(win: Boolean) {
        won = win
    }
}

class ProbeStrategy(seed: Int) : Strategy {
    private var random: Random = Random(seed)
    private var prevHandValue: Int = 0
    private var currentHandValue: Int = 0
    private var history: Array<IntArray> = Array(3, { IntArray(3, { 2 }) });

    override fun nextHand (): Hand {
        val bet: Int = random.nextInt(getSum(currentHandValue))
        var handvalue: Int = 0
        when {
            bet < history[currentHandValue][0] -> handvalue = 0
            bet < history[currentHandValue][0] + history[currentHandValue][1] -> handvalue = 1
            else -> handvalue = 2
        }
        prevHandValue = currentHandValue
        currentHandValue = handvalue;
        return Hand.getHand(handvalue)
    }

    private fun getSum(handvalue: Int): Int {
        var sum: Int = 0
        for (i in 0..2) {
            sum += history[handvalue][i]
        }
        return sum
    }

    override fun study(win: Boolean): Unit {
        if (win) {
            history[prevHandValue][currentHandValue]++
        } else {
            history[prevHandValue][(currentHandValue + 1) % 3]++
            history[prevHandValue][(currentHandValue + 2) % 3]++
        }
    }
}

class Player (private val name: String, private val strategy: Strategy) { //  () 안의 값들이 실제로 클래스의 iv가 된다. 
    private var wincount: Int = 0
    private var losecount: Int = 0
    private var gamecount: Int = 0

    fun nextHand(): Hand = strategy.nextHand()

    fun win(): Unit {
        strategy.study(true)
        wincount++
        gamecount++
    }

    fun lose(): Unit {
        strategy.study(false)
        losecount++
        gamecount++
    }

    fun even(): Unit {
        gamecount++
    }

    override fun toString(): String {
        return "[$name: $gamecount games, $wincount win, $losecount lose]"
    }
}

fun main(args: Array<String>) {
    if(args.size != 2) {
        println("Usage: java Main randomseed1 randomseed2")
        println("Example: java Main 314 15")
        System.exit(0)
    }

    val seed1: Int = args[0].toInt()
    val seed2: Int = args[1].toInt()
    val player1 : Player = Player("KIM", WinningStrategy(seed1))
    val player2: Player = Player("LEE", WinningStrategy(seed2))
    for (i in 0 until 10000) {
        val nextHand1: Hand = player1.nextHand()
        val nextHand2: Hand = player2.nextHand()

        when {
            nextHand1.isStrongerThan(nextHand2) -> {
                player1.win()
                player2.lose()
                println("Winner: $player1")
            }
            nextHand1.isWeakerThan(nextHand2) -> {
                player1.lose()
                player2.win()
                println("Winner: $player2")
            }
            else -> {
                player1.even()
                player2.even()
                println("Even...")
            }
        }

        println("Total result:")
        println("$player1")
        println("$player2")
    }
}