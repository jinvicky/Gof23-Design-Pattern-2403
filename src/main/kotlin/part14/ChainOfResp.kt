

data class Trouble (val number: Int) {

    override fun toString(): String {
        return "[Trouble $number]"
    }
}

open abstract class Support {
    var name: String = ""
    var next: Support? = null

    fun support(trouble: Trouble) {
        if (resolve(trouble)) {
            done(trouble)
        } else if (next != null) {
            next!!.support(trouble)
        } else {
            fail(trouble)
        }
    }
    
    fun setNext(next: Support): Support {
        this.next = next
        return next
    }
    
    override fun toString(): String {
        return "[$name]"
    }
    
    protected abstract fun resolve(trouble: Trouble): Boolean
    fun done(trouble: Trouble) {
        println("$trouble is resolved by $this.")
    }
    fun fail(trouble: Trouble) {
        println("$trouble cannot be resolved.")
    }
}

class NoSupport : Support {
    constructor(name: String) {
        super.name = name
    }

    override fun resolve(trouble: Trouble): Boolean {
        return false
    }
}

class LimitSupport : Support {
    var limit: Int = 0
    constructor(name: String, limit: Int) {
        super.name = name
        this.limit = limit
    }

    override fun resolve(trouble: Trouble): Boolean {
        return trouble.number < limit
    }
}

class OddSupport : Support {
    constructor(name: String) {
        super.name = name
    }

    override fun resolve(trouble: Trouble): Boolean {
        return trouble.number % 2 == 1
    }
}

class SpecialSupport : Support {
    var number: Int = 0
    constructor(name: String, number: Int) {
        super.name = name
        this.number = number
    }

    override fun resolve(trouble: Trouble): Boolean {
        return trouble.number == number
    }
}

fun main() {
    val alice = NoSupport("Alice")
    val bob = LimitSupport("Bob", 100)
    val charlie = SpecialSupport("Charlie", 429)
    val diana = LimitSupport("Diana", 200)
    val elmo = OddSupport("Elmo")
    val fred = LimitSupport("Fred", 300)

    alice.setNext(bob).setNext(charlie).setNext(diana).setNext(elmo).setNext(fred)

    for (i in 0..499 step 33) {
        alice.support(Trouble(i))
    }
}
