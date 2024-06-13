package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.breizhcamp.konter.domain.entities.Speaker
import org.breizhcamp.konter.domain.use_cases.ports.SpeakerPort
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.core.io.ResourceLoader
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(SpeakerImport::class)
class SpeakerImportTest {

    @MockkBean
    private lateinit var speakerPort: SpeakerPort

    @Autowired
    private lateinit var speakerImport: SpeakerImport

    @Autowired
    private lateinit var resourceLoader: ResourceLoader

    private val speakers = listOf(
        Speaker(
            id = UUID.fromString("dd41f4a0-d300-4692-977a-14de9aa5db72"),
            lastname = "Hammond",
            firstname = "John",
            email = "john.hammond@jurassic-park.fr",
            tagLine = "J'ai dépensé sans compter",
            bio = "Président Directeur Général chez InGen LLC",
            profilePicture = "https://static.wikia.nocookie.net/jurassicpark/images/d/d3/Hammond_%281993%29.PNG/revision/latest?cb=20210227191455&path-prefix=fr"
        ),
        Speaker(
            id = UUID.fromString("7b1cce9c-59e4-4d6e-9ee2-9e20f1224ce4"),
            lastname = "Brown",
            firstname = "Emmett",
            email = "doc@back-to-the-future.net",
            tagLine = "Là où on va on n'a pas besoin de route",
            bio = "Inventeur du voyage dans le temps, mais surtout fan d'horloges :)",
            profilePicture = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/34/Emmett_Brown_Back_to_the_Future_Universal_Studios_Florida.JPG/800px-Emmett_Brown_Back_to_the_Future_Universal_Studios_Florida.JPG"
        ),
        Speaker(
            id = UUID.fromString("f9840ccd-0c22-4c6d-a712-be2c81847222"),
            lastname = "Kent",
            firstname = "Clark",
            email = "clark.kent@daily-bugle.com",
            tagLine = "Mais non, je n'ai rien à voir avec ce Superman dont vous parlez",
            bio = "Journaliste au Daily Bugle le jour, et mec normal la nuit",
            profilePicture = "https://i.pinimg.com/originals/f2/35/78/f235783e1a67ed5bb6de83afd324dcaa.jpg"
        ),
        Speaker(
            id = UUID.fromString("17ac8d41-3167-455a-ba14-de73b20a4334"),
            lastname = "Wayne",
            firstname = "Bruce",
            email = "bruce.wayne@wayne-industries.com",
            tagLine = "You didn't get the memo?",
            bio = "Ancien PDG de Wayne Industries, je consacre maintenant ma vie à mes oeuvres de bienfaisance",
            profilePicture = "https://tse1.mm.bing.net/th?id=OIP.lyXmlreDj9JKFtF5gFAPwQHaKF&pid=Api"
        )
    )

    @Test
    fun `importCsv should read all the values excluding the header, log and call the port to save them`(output: CapturedOutput) {
        val file = resourceLoader.getResource("classpath:csv/speaker-test.csv").inputStream
        for (speaker in speakers) {
            every { speakerPort.save(speaker) } just Runs
        }

        speakerImport.importCsv(file)
        assert(output.contains("Saving [${speakers.size}] Speakers"))

        for (speaker in speakers) {
            verify { speakerPort.save(speaker) }
        }
    }
}