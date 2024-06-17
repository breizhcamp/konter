package org.breizhcamp.konter.domain.use_cases

import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import org.breizhcamp.konter.domain.entities.Evaluation
import org.breizhcamp.konter.domain.entities.Event
import org.breizhcamp.konter.domain.entities.Session
import org.breizhcamp.konter.domain.entities.enums.SessionFormatEnum
import org.breizhcamp.konter.domain.entities.enums.SessionNiveauEnum
import org.breizhcamp.konter.domain.entities.enums.SessionStatusEnum
import org.breizhcamp.konter.domain.entities.enums.SessionThemeEnum
import org.breizhcamp.konter.domain.use_cases.ports.EventPort
import org.breizhcamp.konter.domain.use_cases.ports.KalonPort
import org.breizhcamp.konter.domain.use_cases.ports.SessionPort
import org.breizhcamp.konter.domain.use_cases.ports.SpeakerPort
import org.breizhcamp.konter.testUtils.EventGen
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.core.io.ResourceLoader
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.InputStream
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.Month
import java.util.*

@ExtendWith(SpringExtension::class)
@ExtendWith(OutputCaptureExtension::class)
@WebMvcTest(SessionImport::class)
class SessionImportTest {

    @MockkBean
    private lateinit var sessionPort: SessionPort

    @MockkBean
    private lateinit var speakerPort: SpeakerPort

    @MockkBean
    private lateinit var eventPort: EventPort

    @MockkBean
    private lateinit var kalonPort: KalonPort

    @Autowired
    private lateinit var sessionImport: SessionImport

    @Autowired
    private lateinit var loader: ResourceLoader

    private lateinit var event: Event
    private lateinit var sessions: List<Session>
    private lateinit var evals: List<Evaluation>

    private val hammond = SpeakerImportTest.speakers.find { it.id == UUID.fromString("dd41f4a0-d300-4692-977a-14de9aa5db72") }!!
    private val brown = SpeakerImportTest.speakers.find { it.id == UUID.fromString("7b1cce9c-59e4-4d6e-9ee2-9e20f1224ce4") }!!
    private val kent = SpeakerImportTest.speakers.find { it.id == UUID.fromString("17ac8d41-3167-455a-ba14-de73b20a4334") }!!
    private val wayne = SpeakerImportTest.speakers.find { it.id == UUID.fromString("f9840ccd-0c22-4c6d-a712-be2c81847222") }!!

    private lateinit var sessionFile: InputStream
    private lateinit var evaluationFile: InputStream

    @BeforeEach
    fun setUp() {
        sessionFile = loader.getResource("classpath:csv/session-test.csv").inputStream
        evaluationFile = loader.getResource("classpath:csv/evaluation-test.csv").inputStream
        event = EventGen().generateOne()
        val hammondSession = Session(
            id = 101,
            title = "PSQL, une technologie de dinosaures ? \uD83D\uDC74\uD83C\uDFFB",
            description = "PostgreSQL approche ses 30 ans de carrière, et reste un des systèmes de gestion de base de données les plus utilisés à ce jour\n" +
                    "\n" +
                    "À travers l'exemple de notre parc, découvrez pourquoi PSQL reste un système en accord avec notre temps et comment ses extensions permettent d'intégrer tous les types de données utilisés dans notre entreprise, allant de la localisation géographique des enclos aux codes génétiques de nos dinosaures.",
            owner = hammond,
            speakers = listOf(hammond),
            format = SessionFormatEnum.CONFERENCE,
            theme = SessionThemeEnum.ARCHI,
            niveau = SessionNiveauEnum.INTRO,
            status = SessionStatusEnum.NOMINATED,
            submitted = LocalDateTime.of(2024, Month.APRIL, 14, 17, 32),
            ownerNotes = "Après un bref rappel de ce qu'est une base de données relationnelle, cette conférence abordera les spécificités de Postgres, puis une présentation de notre cas d'usage. Ce cas d'usage a aussi pour but de présenter des extensions peu utilisées mais qui peuvent convenir à beaucoup de projets modernes, comme la gestion de données de localisation géographiques, de données vectorielles & de fichiers de grandes tailles",
            event = event,
            videoURL = null,
            rating = null,
            slot = null
        )
        val brownSession = Session(
            id = 203,
            title = "Le futur de l'IA",
            description = "Conférence sur les futures formes de l'IA, et comment le monde en sera impacté",
            owner = brown,
            speakers = listOf(brown),
            format = SessionFormatEnum.CONFERENCE,
            theme = SessionThemeEnum.AI,
            niveau = SessionNiveauEnum.INTRO,
            status = SessionStatusEnum.NOMINATED,
            submitted = LocalDateTime.of(2024, Month.APRIL, 15, 8, 12),
            ownerNotes = "",
            event = event,
            videoURL = null,
            rating = null,
            slot = null
        )
        val wayneKentSession = Session(
            id = 207,
            title = "Dévoilons les Secrets de GitHub Actions",
            description = "Découvrez comment gérer les secrets dans GitHub Actions pour sécuriser les workflows CI/CD.\n" +
                    "\n" +
                    "Clark aborde la création et la gestion des secrets, tandis que Bruce traite des techniques avancées comme la rotation et le partage sécurisé.\n" +
                    "\n" +
                    "Ensemble, nous montrons comment garantir des workflows efficaces et sécurisés dans GitHub Actions.",
            owner = wayne,
            speakers = listOf(wayne, kent),
            format = SessionFormatEnum.TOOL,
            theme = SessionThemeEnum.SEC,
            niveau = SessionNiveauEnum.INTRO,
            status = SessionStatusEnum.NOMINATED,
            submitted = LocalDateTime.of(2024, Month.APRIL, 15, 8, 48),
            ownerNotes = "**Résumé**\n" +
                    "\n" +
                    "*Dévoiler les Secrets de GitHub Actions : démonstration*\n" +
                    "\n" +
                    "Découvrez comment gérer les secrets dans GitHub Actions, essentiels pour automatiser les workflows tout en sécurisant des éléments comme des clés d'API, des tokens d'authentification, ...\n" +
                    "\n" +
                    "Clark Kent, journaliste au Daily Bugle présente les bases de la gestion des secrets :\n" +
                    "\n" +
                    "1. **Définition des Secrets :** Création et gestion des secrets dans les paramètres du dépôt.\n" +
                    "2. **Contrôle d'Accès :** Accès réservé aux workflows spécifiques.\n" +
                    "3. **Segmentation par Environnement :** Configuration des secrets pour différents environnements (développement, staging, production).\n" +
                    "\n" +
                    "Bruce Wayne, ex-PDG de Wayne Industries, aborde quant à lui des techniques plus avancées pour la protection de vos secrets :\n" +
                    "\n" +
                    "1. **Rotation des Secrets :** Mise à jour régulière et automatisée.\n" +
                    "2. **Audit et Surveillance :** Suivi de l'utilisation des secrets.\n" +
                    "3. **Partage de Secrets :** Pratiques sécurisées pour partager entre différents dépôts.\n" +
                    "\n" +
                    "Ensemble, nous fournissons un guide complet pour gérer les secrets dans GitHub Actions, en soulignant l'importance de la sécurité dans les pipelines CI/CD. À la fin de cette session, vous saurez comment utiliser GitHub Actions pour des workflows efficaces et sécurisés.",
            event = event,
            videoURL = null,
            rating = null,
            slot = null
        )
        sessions  = listOf(hammondSession, brownSession, wayneKentSession)
        evals = listOf(
            Evaluation(hammondSession, BigDecimal.valueOf(3.9)),
            Evaluation(brownSession, BigDecimal.valueOf(2.3)),
            Evaluation(wayneKentSession, BigDecimal.valueOf(4.2))
        )
        every { eventPort.getById(event.id) } returns event
    }

    @Test
    fun `importCsv should call kalonPort to update the events if the event was not found, and stop if it is not in kalon either`(
        output: CapturedOutput
    ) {
        every { eventPort.existsById(event.id) } returns false
        every { kalonPort.getEvents() } returns emptyList()
        every { eventPort.save(emptyList()) } just Runs

        sessionImport.importCsv(event.id, sessionFile)
        assert(output.contains("No Event with id=${event.id} found, importing from Kalon"))
        assert(output.contains("No Event with id=${event.id} found, exiting"))

        verify(exactly = 2) { eventPort.existsById(event.id) }
        verify(exactly = 0) { eventPort.getById(event.id) }
    }

    @Test
    fun `importCsv should call kalonPort to update the events if the event was not found, and continue if it was found in kalon`(
        output: CapturedOutput
    ) {
        coEvery { eventPort.existsById(event.id) } returnsMany listOf(false, true)
        every { kalonPort.getEvents() } returns listOf(event)
        every { eventPort.save(listOf(event)) } just Runs

        for (speaker in SpeakerImportTest.speakers) {
            every { speakerPort.get(speaker.id) } returns speaker
            every {
                speakerPort.getByNameAndEmail(
                    "${speaker.firstname} ${speaker.lastname}",
                    speaker.email
                )
            } returns speaker
        }

        every { sessionPort.import(any()) } just Runs

        sessionImport.importCsv(event.id, sessionFile)
        assert(output.contains("No Event with id=${event.id} found, importing from Kalon"))

        verify(exactly = 2) { eventPort.existsById(event.id) }
        verify { kalonPort.getEvents() }
        verify { eventPort.save(listOf(event)) }
        verify { eventPort.getById(event.id) }
    }

    @Test
    fun `importCsv should not kalonPort if the event wad found in the repo, retrieve session owners and speakers and log the number of imported sessions`(
        output: CapturedOutput
    ) {
        every { eventPort.existsById(event.id) } returns true
        every { kalonPort.getEvents() } returns listOf(event)
        every { eventPort.save(listOf(event)) } just Runs

        for (speaker in SpeakerImportTest.speakers) {
            every { speakerPort.get(speaker.id) } returns speaker
            every {
                speakerPort.getByNameAndEmail(
                    "${speaker.firstname} ${speaker.lastname}",
                    speaker.email
                )
            } returns speaker
        }
        every { sessionPort.import(any()) } just Runs

        sessionImport.importCsv(event.id, sessionFile)
        assert(!output.contains("No Event with id=${event.id} found, importing from Kalon"))

        verify(exactly = 2) { eventPort.existsById(event.id) }
        verify(exactly = 0) { kalonPort.getEvents() }
        verify(exactly = 0) { eventPort.save(listOf(event)) }
        verify { eventPort.getById(event.id) }
    }

    @Test
    fun importEvaluationCsv(output: CapturedOutput) {
        for (session in sessions) {
            every { sessionPort.getById(session.id) } returns session
        }
        for (eval in evals) {
            every { sessionPort.saveEvaluation(eval) } just Runs
        }

        sessionImport.importEvaluationCsv(evaluationFile)

        assert(output.contains("Saving [${evals.size}] Evaluations"))
    }
}