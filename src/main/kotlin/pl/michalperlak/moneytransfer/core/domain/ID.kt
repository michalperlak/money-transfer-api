package pl.michalperlak.moneytransfer.core.domain

import java.util.Objects
import java.util.UUID

abstract class ID protected constructor(private val id: UUID) {
    override fun equals(other: Any?): Boolean {
        if (other == null || this.javaClass != other.javaClass) {
            return false
        }
        val that = other as ID
        return id == that.id
    }

    override fun hashCode(): Int = Objects.hash(id)

    override fun toString(): String = id.toString()
}