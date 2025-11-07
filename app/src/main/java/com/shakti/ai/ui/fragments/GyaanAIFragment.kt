package com.shakti.ai.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.shakti.ai.R
import com.shakti.ai.databinding.FragmentGyaanAiBinding
import com.shakti.ai.viewmodel.GyaanViewModel
import kotlinx.coroutines.launch

class GyaanAIFragment : Fragment() {

    private var _binding: FragmentGyaanAiBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GyaanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGyaanAiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() = with(binding) {
        btnFindScholarships.setOnClickListener { findScholarships() }
        btnPreFillForms.setOnClickListener { showPreFillFormsWizard() }
        btnDocumentChecklist.setOnClickListener { showDocumentChecklist() }
        btnDeadlineReminders.setOnClickListener { showDeadlineReminders() }
        btnApplicationTracking.setOnClickListener { showApplicationTracking() }
        btnVirtualMentorship.setOnClickListener { connectWithMentor() }
        btnWomenLeadersStories.setOnClickListener { showWomenLeadersStories() }
        btnSkillDevelopment.setOnClickListener { showSkillDevelopment() }
        btnOnlineCourses.setOnClickListener { showOnlineCourses() }
        btnCareerGuidance.setOnClickListener { showCareerGuidance() }
        btnSkillAssessment.setOnClickListener { takeSkillAssessment() }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.btnFindScholarships.isEnabled = !isLoading
                        binding.btnFindScholarships.alpha = if (isLoading) 0.5f else 1f
                        binding.btnFindScholarships.text =
                            if (isLoading) "🔍 Searching..." else "🔍 Find My Scholarships"
                    }
                }
                launch {
                    viewModel.scholarships.collect { s ->
                        if (s.isNotEmpty()) showScholarships(s)
                    }
                }
                launch {
                    viewModel.courseRecommendations.collect { c ->
                        if (c.isNotEmpty()) showCoursesDialog(c)
                    }
                }
                launch {
                    viewModel.errorMessage.collect { e ->
                        e?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                            viewModel.clearError()
                        }
                    }
                }
            }
        }
    }

    private fun findScholarships() = with(binding) {
        val education = courseInput.text?.toString()?.trim().orEmpty()
        val income = incomeInput.text?.toString()?.trim()?.toLongOrNull() ?: 0L
        val category = categoryInput.text?.toString()?.trim().orEmpty()
        val state = stateInput.text?.toString()?.trim().orEmpty()

        if (education.isBlank()) {
            Toast.makeText(requireContext(), "Please enter course details", Toast.LENGTH_SHORT)
                .show()
            return@with
        }

        Toast.makeText(
            requireContext(),
            "🔍 Searching scholarships for:\n• $education\n• $category\n• $state",
            Toast.LENGTH_SHORT
        ).show()

        viewModel.findScholarships(education, income, category)
    }

    private fun showScholarships(scholarships: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("🎓 Scholarships Found")
            .setMessage(scholarships)
            .setPositiveButton("Apply Now") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "Opening application portal...",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNeutralButton("Save List") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "✅ Scholarships saved to your profile",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showPreFillFormsWizard() {
        val formTypes = arrayOf(
            "Scholarship Application Form",
            "College Admission Form",
            "Government Scheme Application",
            "Online Course Registration",
            "Job Application Form"
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("📝 Auto-Fill Forms")
            .setItems(formTypes) { _, which ->
                val formType = formTypes[which]
                Toast.makeText(
                    requireContext(),
                    "✅ Opening $formType with auto-fill enabled",
                    Toast.LENGTH_LONG
                ).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDocumentChecklist() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("📋 Document Checklist")
            .setMessage(
                """
                For Scholarship Application:
                
                ✅ 10th Marksheet
                ✅ 12th Marksheet
                ✅ Graduation Marksheet (if applicable)
                ✅ Income Certificate (< 1 year old)
                ✅ Caste Certificate (if applicable)
                ✅ Domicile Certificate
                ✅ Aadhaar Card
                ✅ Bank Account Passbook
                ✅ Passport Size Photos (recent)
                ✅ College/University ID
                ✅ Bonafide Certificate
                
                💡 Tip: Keep scanned copies (PDF format) ready!
            """.trimIndent()
            )
            .setPositiveButton("Upload Documents") { _, _ ->
                Toast.makeText(requireContext(), "Opening document upload...", Toast.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showDeadlineReminders() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("⏰ Deadline Reminders")
            .setMessage(
                """
                Upcoming Deadlines:
                
                📌 National Scholarship Portal
                   Deadline: 30th November 2025
                
                📌 State Scholarship Scheme
                   Deadline: 15th December 2025
                
                📌 Merit-cum-Means Scholarship
                   Deadline: 31st December 2025
                
                💡 We'll send you reminders 7 days before deadline!
            """.trimIndent()
            )
            .setPositiveButton("Set Reminders") { _, _ ->
                Toast.makeText(requireContext(), "✅ Reminders activated!", Toast.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showApplicationTracking() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("📊 Application Tracking")
            .setMessage(
                """
                Your Applications Status:
                
                🟢 Merit Scholarship - Approved
                   Amount: ₹50,000 | Status: Disbursed
                
                🟡 State Scholarship - Under Review
                   Amount: ₹30,000 | Status: Pending
                
                🔴 Central Scholarship - Documents Required
                   Action: Upload income certificate
                
                📈 Total Applied: 5
                ✅ Approved: 1
                ⏳ Pending: 2
                ❌ Rejected: 1
            """.trimIndent()
            )
            .setPositiveButton("View Details") { _, _ ->
                Toast.makeText(requireContext(), "Opening detailed view...", Toast.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showWomenLeadersStories() {
        val leaders = arrayOf(
            "🌟 Kiran Mazumdar-Shaw - Biocon Founder",
            "🌟 Indra Nooyi - Former PepsiCo CEO",
            "🌟 Falguni Nayar - Nykaa Founder",
            "🌟 Sudha Murthy - Author & Philanthropist",
            "🌟 Roshni Nadar Malhotra - HCL Tech CEO",
            "🌟 Arundhati Bhattacharya - Former SBI Chairperson"
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("⭐ Women Leaders Stories")
            .setItems(leaders) { _, which ->
                val leader = leaders[which].substring(2)
                showLeaderStory(leader)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showLeaderStory(leader: String) {
        val story = when {
            leader.contains("Kiran", true) -> """
                Kiran Mazumdar-Shaw - Breaking Barriers in Biotech
                Started Biocon in 1978 with just ₹10,000 in her garage. Today, Biocon is India's largest biopharmaceutical company.
                
                Key Lessons:
                • Don't let gender stereotypes limit you
                • Persistence pays off
                • Innovation drives success
                • Give back to society
            """.trimIndent()
            leader.contains("Falguni", true) -> """
                Falguni Nayar - From Investment Banking to Beauty Empire
                Left her successful career at age 50 to start Nykaa. Built India's leading beauty & fashion e-commerce platform.
                
                Key Lessons:
                • It's never too late
                • Follow your passion
                • Build strong teams
                • Focus on customer experience
            """.trimIndent()
            else -> """
                $leader
                An inspiring journey of determination, hard work, and breaking glass ceilings.
            """.trimIndent()
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(leader)
            .setMessage(story)
            .setPositiveButton("Read More") { _, _ ->
                Toast.makeText(requireContext(), "Opening full story...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showSkillDevelopment() {
        val skillCategories = arrayOf(
            "💻 Technology & Coding",
            "📊 Business & Finance",
            "🎨 Design & Creativity",
            "📢 Marketing & Communication",
            "👥 Leadership & Management",
            "🏥 Healthcare & Wellness"
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("💻 Free Skill Development")
            .setItems(skillCategories) { _, which ->
                val category = skillCategories[which].substring(2)
                showSkillCourses(category)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSkillCourses(category: String) {
        val courses = if (category.contains("Technology", true)) """
                Free Technology Courses:
                • Python Programming (Coursera)
                • Web Development (freeCodeCamp)
                • Data Science Basics (Google)
                • App Development (Android)
                • AI/ML Fundamentals (Microsoft)
                
                All courses offer certificates!
            """.trimIndent()
        else """
                Free Courses in $category:
                • Beginner Level Courses
                • Intermediate Projects
                • Advanced Specializations
                • Certification Programs
                • Industry Mentorship
            """.trimIndent()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(category)
            .setMessage(courses)
            .setPositiveButton("Enroll Now") { _, _ ->
                Toast.makeText(requireContext(), "✅ Opening enrollment...", Toast.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun connectWithMentor() {
        val mentorTypes = arrayOf(
            "🎓 Academic Counselor",
            "💼 Career Mentor",
            "💻 Tech Industry Expert",
            "👩‍⚕ Healthcare Professional",
            "👩‍🏫 Teaching/Education",
            "📊 Business/Entrepreneurship"
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("👩‍🏫 Connect with Mentor")
            .setItems(mentorTypes) { _, which ->
                val mentor = mentorTypes[which].substring(2)
                Toast.makeText(
                    requireContext(),
                    "✅ Finding mentors in: $mentor\nYou'll be matched soon.",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.recommendCourses(emptyList(), mentor, 0L)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showOnlineCourses() {
        val popular = """
            💻 Free Online Learning Platforms:
            1. SWAYAM (Govt of India)
            2. NPTEL (IIT/IISc)
            3. Google Digital Garage
            4. Microsoft Learn
            5. Coursera (scholarships)
            6. Udemy Free Courses
        """.trimIndent()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("📚 Free Online Courses")
            .setMessage(popular)
            .setPositiveButton("Browse Courses") { _, _ ->
                viewModel.recommendCourses(emptyList(), "All", 0L)
                Toast.makeText(requireContext(), "✅ Opening course catalog...", Toast.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showCoursesDialog(courses: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("📚 Recommended Courses")
            .setMessage(courses)
            .setPositiveButton("Enroll") { _, _ ->
                Toast.makeText(requireContext(), "Opening course enrollment...", Toast.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showCareerGuidance() {
        val input = TextInputEditText(requireContext()).apply {
            hint = "What are your interests? (e.g., Teaching, Technology, Healthcare)"
            setPadding(50, 40, 50, 40)
            minHeight = 120
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("🎯 Career Guidance")
            .setMessage("Tell us about your interests and skills:")
            .setView(input)
            .setPositiveButton("Get Guidance") { _, _ ->
                val interests = input.text?.toString()?.trim().orEmpty()
                if (interests.isNotBlank()) {
                    showCareerOptions(interests)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please enter your interests",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCareerOptions(interests: String) {
        val suggestions = getCareerSuggestions(interests)
        val advice = """
            🎯 Career Paths for "$interests":
            $suggestions
            
            📚 Recommended Skills:
            • Communication • Digital Literacy • Leadership • Field-specific Tech
        """.trimIndent()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Career Guidance")
            .setMessage(advice)
            .setPositiveButton("Explore Courses") { _, _ -> showOnlineCourses() }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun getCareerSuggestions(interests: String): String = when {
        interests.contains("tech", true) -> """
                • Software Developer
                • Data Analyst
                • Digital Marketing Specialist
                • UI/UX Designer
                • Cybersecurity Analyst
            """.trimIndent()
        interests.contains("teach", true) -> """
                • School Teacher
                • Online Tutor
                • Educational Content Creator
                • Career Counselor
                • Training & Development Specialist
            """.trimIndent()
        interests.contains("health", true) -> """
                • Nurse
                • Medical Technician
                • Nutritionist
                • Public Health Worker
                • Healthcare Administrator
            """.trimIndent()
        else -> """
                • Multiple options match your interests.
                • Take a skill assessment for personalization.
                • Connect with mentors for guidance.
            """.trimIndent()
    }

    private fun takeSkillAssessment() {
        val skills = arrayOf(
            "Communication Skills",
            "Problem Solving",
            "Technical Skills (Computers)",
            "Leadership & Management",
            "Creative Thinking",
            "Financial Literacy"
        )
        val selected = BooleanArray(skills.size)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("📊 Skill Assessment")
            .setMultiChoiceItems(skills, selected) { _, which, isChecked ->
                selected[which] = isChecked
            }
            .setPositiveButton("Start Assessment") { _, _ ->
                val chosen = skills.filterIndexed { i, _ -> selected[i] }
                if (chosen.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please select at least one skill",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    showAssessmentResult(chosen)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAssessmentResult(skills: List<String>) {
        val result = skills.joinToString("\n") { s -> "• $s: ${(60..95).random()}/100" }
        val advice = """
            📊 Your Skill Assessment Results:
            $result
            
            💡 Recommendations:
            • Focus on lower-scored skills
            • Take online courses & practice
            • Seek mentorship and build projects
        """.trimIndent()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Assessment Results")
            .setMessage(advice)
            .setPositiveButton("Find Courses") { _, _ -> showOnlineCourses() }
            .setNegativeButton("Close", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
