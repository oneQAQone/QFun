package me.yxp.qfun.utils.scheduler

data class ScheduledTask(
    val id: String,
    val targetTime: Long,
    val action: () -> Unit
) : Comparable<ScheduledTask> {
    override fun compareTo(other: ScheduledTask): Int {
        return this.targetTime.compareTo(other.targetTime)
    }
}