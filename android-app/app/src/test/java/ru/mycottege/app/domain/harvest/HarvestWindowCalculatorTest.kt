package ru.mycottege.app.domain.harvest

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.mycottege.app.domain.crops.CropSpec
import ru.mycottege.app.domain.crops.CropId
import java.time.LocalDate

class HarvestWindowCalculatorTest {

  @Test
  fun radish_window_is_calculated_correctly() {
    val planted = LocalDate.of(2026, 5, 1)
    val crop = CropSpec(CropId.RADISH, 20, 30)

    val window = HarvestWindowCalculator.calculate(planted, crop)

    assertEquals(LocalDate.of(2026, 5, 21), window.start)
    assertEquals(LocalDate.of(2026, 5, 31), window.end)
  }
}
