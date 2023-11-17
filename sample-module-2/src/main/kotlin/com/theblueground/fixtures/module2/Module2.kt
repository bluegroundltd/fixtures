package com.theblueground.fixtures.module2

import com.theblueground.fixtures.Fixture
import com.theblueground.fixtures.ModularizedFixture
import com.theblueground.fixtures.module1.Module1

@Fixture
data class Module2(@ModularizedFixture val module1: Module1)
