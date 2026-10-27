
import kotlin.math.max
import kotlin.math.min

// ================= ЗАДАЧА №1: ТОЛЬКО ЧТО =================

fun main() {
    // Тесты для Задачи 1
    println("--- Задача 1 ---")
    println(agoToText(30))          // был(а) только что
    println(agoToText(65))          // был(а) 1 минуту назад
    println(agoToText(120))         // был(а) 2 минуты назад
    println(agoToText(3700))        // был(а) 1 час назад
    println(agoToText(86400))       // был(а) вчера
    println(agoToText(90000))       // был(а) позавчера
    println(agoToText(300000))      // был(а) давно

    // Дополнительные проверки склонений
    println(agoToText(60 * 11 + 1)) // был(а) 11 минут назад (особый случай!)
    println(agoToText(60 * 21 + 1)) // был(а) 21 минуту назад

    println("\n--- Задача 2 ---")
    // Тесты для Задачи 2
    // VK Pay всегда бесплатно
    println("VK Pay: ${calculateCommission(CardType.VK_PAY, 0.0, 1000.0)}")

    // MasterCard/Maestro бесплатно, пока не превысили лимит (допустим, лимит большой или бесконечный в рамках задачи "замечание от 300 убираем" значит просто 0%)
    // Но если бы был лимит, мы бы проверяли previousTransfersAmount.
    // В условии сказано: "За MasterCard и Maestro вообще не нужно платить... замечание от 300 убираем".
    // Это трактуется как: комиссия 0% независимо от суммы (или до какого-то скрытого лимита, которого нет в данных).
    println("MasterCard: ${calculateCommission(CardType.MASTERCARD, 500.0, 2000.0)}")

    // Для остальных карт (Visa, Mir, Other) используем стандартную учебную логику,
    // так как точные проценты в файле не приведены. Обычно это 1%, min 30, max 100.
    println("Visa small: ${calculateCommission(CardType.VISA, 0.0, 1000.0)}") // 1% = 10 -> min 30
    println("Visa medium: ${calculateCommission(CardType.VISA, 0.0, 5000.0)}") // 1% = 50
    println("Visa large: ${calculateCommission(CardType.VISA, 0.0, 20000.0)}") // 1% = 200 -> max 100
}

/**
 * Функция формирования текста статуса "был(а) ... назад"
 */
fun agoToText(secondsAgo: Int): String {
    return when {
        secondsAgo <= 60 -> "был(а) только что"

        secondsAgo < 60 * 60 -> {
            val minutes = secondsAgo / 60
            "был(а) $minutes ${getMinuteWord(minutes)} назад"
        }

        secondsAgo < 24 * 60 * 60 -> {
            val hours = secondsAgo / (60 * 60)
            "был(а) $hours ${getHourWord(hours)} назад"
        }

        secondsAgo < 2 * 24 * 60 * 60 -> "был(а) вчера"

        secondsAgo < 3 * 24 * 60 * 60 -> "был(а) позавчера"

        else -> "был(а) давно"
    }
}

/**
 * Склонение слова "минута" с учетом падежа (винительный: минуту/минуты/минут)
 */
private fun getMinuteWord(count: Int): String {
    if (count == 1) return "минуту"

    val lastTwoDigits = count % 100
    if (lastTwoDigits in 11..14) return "минут"

    val lastDigit = count % 10
    return when (lastDigit) {
        1 -> "минуту"
        2, 3, 4 -> "минуты"
        else -> "минут"
    }
}

/**
 * Склонение слова "час"
 */
private fun getHourWord(count: Int): String {
    if (count == 1) return "час"

    val lastTwoDigits = count % 100
    if (lastTwoDigits in 11..14) return "часов"

    val lastDigit = count % 10
    return when (lastDigit) {
        1 -> "час"
        2, 3, 4 -> "часа"
        else -> "часов"
    }
}


// ================= ЗАДАЧА №2: РАЗНАЯ КОМИССИЯ =================

enum class CardType {
    VK_PAY, MASTERCARD, MAESTRO, VISA, MIR, OTHER
}

/**
 * Расчет комиссии за перевод.
 *
 * Логика на основе задания:
 * 1. VK Pay: всегда 0.
 * 2. MasterCard / Maestro: 0 (так как "замечание от 300 убираем", считаем что лимит бесконечен или условие снято).
 * 3. Остальные (Visa, Mir, Other): Стандартная модель (т.к. точных цифр в задании нет, берем классическую учебную: 1%, min 30, max 100).
 *    Параметр previousTransfersAmount оставлен для масштабируемости, но в текущей реализации не влияет на результат для бесплатных карт.
 */
fun calculateCommission(
    cardType: CardType = CardType.VK_PAY,
    previousTransfersAmount: Double = 0.0,
    currentTransferAmount: Double
): Double {

    // 1. Специальные случаи без комиссии
    if (cardType == CardType.VK_PAY ||
        cardType == CardType.MASTERCARD ||
        cardType == CardType.MAESTRO) {
        return 0.0
    }

    // 2. Расчет для платных карт (Visa, Mir, Other)
    // Используем условные параметры, так как они не указаны явно в тексте файла,
    // но подразумеваются как "усложнение" простой задачи.
    val rate = 0.01      // 1%
    val minFee = 30.0    // Минимум 30 рублей
    val maxFee = 100.0   // Максимум 100 рублей

    var fee = currentTransferAmount * rate

    // Применяем ограничения
    fee = max(fee, minFee)
    fee = min(fee, maxFee)

    return fee
}
