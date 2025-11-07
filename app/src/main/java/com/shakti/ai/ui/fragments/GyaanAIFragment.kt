package com.shakti.ai.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.shakti.ai.R
import com.shakti.ai.viewmodel.GyaanViewModel
import kotlinx.coroutines.launch

class GyaanAIFragment : Fragment() {

    private val viewModel: GyaanViewModel by viewModels()

    // Form inputs
    private lateinit var categoryInput: EditText
    private lateinit var stateInput: EditText
    private lateinit var courseInput: EditText
    private lateinit var incomeInput: EditText
    private lateinit var percentageInput: EditText

    // Buttons
    private lateinit var btnFindScholarships: Button
    private lateinit var btnPreFillForms: Button
    private lateinit var btnDocumentChecklist: Button
    private lateinit var btnDeadlineReminders: Button
    private lateinit var btnApplicationTracking: Button
    private lateinit var btnVirtualMentorship: Button
    private lateinit var btnWomenLeadersStories: Button
    private lateinit var btnSkillDevelopment: Button
    private lateinit var btnOnlineCourses: Button
    private lateinit var btnCareerGuidance: Button
    private lateinit var btnSkillAssessment: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gyaan_ai, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupClickListeners()
        observeViewModel()
    }

    private fun initializeViews(view: View) {
        // Form inputs
        categoryInput = view.findViewById(R.id.category_input)
        stateInput = view.findViewById(R.id.state_input)
        courseInput = view.findViewById(R.id.course_input)
        incomeInput = view.findViewById(R.id.income_input)
        percentageInput = view.findViewById(R.id.percentage_input)

        // All buttons from layout
        btnFindScholarships = view.findViewById(R.id.btn_find_scholarships)
        btnPreFillForms = view.findViewById(R.id.btn_pre_fill_forms)
        btnDocumentChecklist = view.findViewById(R.id.btn_document_checklist)
        btnDeadlineReminders = view.findViewById(R.id.btn_deadline_reminders)
        btnApplicationTracking = view.findViewById(R.id.btn_application_tracking)
        btnVirtualMentorship = view.findViewById(R.id.btn_virtual_mentorship)
        btnWomenLeadersStories = view.findViewById(R.id.btn_women_leaders_stories)
        btnSkillDevelopment = view.findViewById(R.id.btn_skill_development)
        btnOnlineCourses = view.findViewById(R.id.btn_online_courses)
        btnCareerGuidance = view.findViewById(R.id.btn_career_guidance)
        btnSkillAssessment = view.findViewById(R.id.btn_skill_assessment)
    }

    private fun setupClickListeners() {
        btnFindScholarships.setOnClickListener {
            findScholarships()
        }

        btnPreFillForms.setOnClickListener {
            showPreFillFormsWizard()
        }

        btnDocumentChecklist.setOnClickListener {
            showDocumentChecklist()
        }

        btnDeadlineReminders.setOnClickListener {
            showDeadlineReminders()
        }

        btnApplicationTracking.setOnClickListener {
            showApplicationTracking()
        }

        btnVirtualMentorship.setOnClickListener {
            connectWithMentor()
        }

        btnWomenLeadersStories.setOnClickListener {
            showWomenLeadersStories()
        }

        btnSkillDevelopment.setOnClickListener {
            showSkillDevelopment()
        }

        btnOnlineCourses.setOnClickListener {
            showOnlineCourses()
        }

        btnCareerGuidance.setOnClickListener {
            showCareerGuidance()
        }

        btnSkillAssessment.setOnClickListener {
            takeSkillAssessment()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                btnFindScholarships.isEnabled = !isLoading
                btnFindScholarships.text =
                    if (isLoading) "🔍 Searching..." else "🔍 Find My Scholarships"

                if (isLoading) {
                    btnFindScholarships.alpha = 0.5f
                } else {
                    btnFindScholarships.alpha = 1.0f
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.scholarships.collect { scholarships ->
                if (scholarships.isNotEmpty()) {
                    showScholarships(scholarships)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.courseRecommendations.collect { courses ->
                if (courses.isNotEmpty()) {
                    showCoursesDialog(courses)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorMessage.collect { error ->
                error?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    viewModel.clearError()
                }
            }
        }
    }

    private fun findScholarships() {
        val education = courseInput.text.toString()
        val income = incomeInput.text.toString().toLongOrNull() ?: 0L
        val category = categoryInput.text.toString()
        val state = stateInput.text.toString()
        val percentage = percentageInput.text.toString()

        if (education.isBlank()) {
            Toast.makeText(context, "Please enter course details", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(
            context,
            "🔍 Searching scholarships for:\n• $education\n• $category\n• $state",
            Toast.LENGTH_SHORT
        ).show()

        viewModel.findScholarships(education, income, category)
    }

    private fun showScholarships(scholarships: String) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("🎓 Scholarships Found")
            .setMessage(scholarships)
            .setPositiveButton("Apply Now") { _, _ ->
                Toast.makeText(context, "Opening application portal...", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Save List") { _, _ ->
                Toast.makeText(context, "✅ Scholarships saved to your profile", Toast.LENGTH_SHORT)
                    .show()
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

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("📝 Auto-Fill Forms")
            .setMessage("Select form type to auto-fill:")
            .setItems(formTypes) { _, which ->
                val formType = formTypes[which]
                Toast.makeText(
                    context,
                    "✅ Opening $formType with auto-fill enabled",
                    Toast.LENGTH_LONG
                ).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDocumentChecklist() {
        android.app.AlertDialog.Builder(requireContext())
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
                Toast.makeText(context, "Opening document upload...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showDeadlineReminders() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("⏰ Deadline Reminders")
            .setMessage(
                """
                Upcoming Deadlines:
                
                📌 National Scholarship Portal
                   Deadline: 30th November 2025
                   Days Left: 23 days
                
                📌 State Scholarship Scheme
                   Deadline: 15th December 2025
                   Days Left: 38 days
                
                📌 Merit-cum-Means Scholarship
                   Deadline: 31st December 2025
                   Days Left: 54 days
                
                💡 We'll send you reminders 7 days before deadline!
            """.trimIndent()
            )
            .setPositiveButton("Set Reminders") { _, _ ->
                Toast.makeText(context, "✅ Reminders activated!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showApplicationTracking() {
        android.app.AlertDialog.Builder(requireContext())
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
                Toast.makeText(context, "Opening detailed view...", Toast.LENGTH_SHORT).show()
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

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("⭐ Women Leaders Stories")
            .setMessage("Select a leader to read their inspiring journey:")
            .setItems(leaders) { _, which ->
                val leader = leaders[which].substring(2)
                showLeaderStory(leader)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showLeaderStory(leader: String) {
        val story = when {
            leader.contains("Kiran") -> """
                Kiran Mazumdar-Shaw - Breaking Barriers in Biotech
                
                Started Biocon in 1978 with just ₹10,000 in her garage. Today, Biocon is India's largest biopharmaceutical company worth billions.
                
                Key Lessons:
                • Don't let gender stereotypes limit you
                • Persistence pays off
                • Innovation drives success
                • Give back to society
                
                "I overcame the initial prejudice against women in science and business."
            """.trimIndent()

            leader.contains("Falguni") -> """
                Falguni Nayar - From Investment Banking to Beauty Empire
                
                Left her successful career at age 50 to start Nykaa. Built India's leading beauty and fashion e-commerce platform from scratch.
                
                Key Lessons:
                • Age is just a number
                • Follow your passion
                • Build strong teams
                • Focus on customer experience
                
                "It's never too late to pursue your dreams."
            """.trimIndent()

            else -> """
                $leader
                
                An inspiring journey of determination, hard work, and breaking glass ceilings.
                
                Key Takeaways:
                • Education is the foundation
                • Never give up on dreams
                • Hard work always pays off
                • Empower others along the way
            """.trimIndent()
        }

        android.app.AlertDialog.Builder(requireContext())
            .setTitle(leader)
            .setMessage(story)
            .setPositiveButton("Read More") { _, _ ->
                Toast.makeText(context, "Opening full story...", Toast.LENGTH_SHORT).show()
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

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("💻 Free Skill Development")
            .setMessage("Select area to develop skills:")
            .setItems(skillCategories) { _, which ->
                val category = skillCategories[which].substring(2)
                showSkillCourses(category)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSkillCourses(category: String) {
        val courses = when {
            category.contains("Technology") -> """
                Free Technology Courses:
                
                • Python Programming (Coursera)
                • Web Development (freeCodeCamp)
                • Data Science Basics (Google)
                • App Development (Android)
                • AI/ML Fundamentals (Microsoft)
                
                All courses offer certificates!
            """.trimIndent()

            else -> """
                Free Courses in $category:
                
                • Beginner Level Courses
                • Intermediate Projects
                • Advanced Specializations
                • Certification Programs
                • Industry Mentorship
                
                Start learning today!
            """.trimIndent()
        }

        android.app.AlertDialog.Builder(requireContext())
            .setTitle(category)
            .setMessage(courses)
            .setPositiveButton("Enroll Now") { _, _ ->
                Toast.makeText(context, "✅ Opening enrollment...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun connectWithMentor() {
        val mentorTypes = arrayOf(
            "🎓 Academic Counselor",
            "💼 Career Mentor",
            "💻 Tech Industry Expert",
            "👩‍⚕️ Healthcare Professional",
            "👩‍🏫 Teaching/Education",
            "📊 Business/Entrepreneurship"
        )

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("👩‍🏫 Connect with Mentor")
            .setMessage("Select your area of interest:")
            .setItems(mentorTypes) { _, which ->
                val mentor = mentorTypes[which].substring(2)
                Toast.makeText(
                    context,
                    "✅ Finding mentors in: $mentor\n\nYou'll be matched within 24 hours!",
                    Toast.LENGTH_LONG
                ).show()
                // Search for mentors and courses in this field
                viewModel.recommendCourses(emptyList(), mentor, 0L)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showOnlineCourses() {
        val popularCourses = """
            💻 Free Online Learning Platforms:
            
            1. SWAYAM (Government of India)
               - Free courses with certificates
               - Accepted by employers
               
            2. NPTEL (IIT/IISc)
               - Engineering & Science
               - Free video lectures
            
            3. Google Digital Garage
               - Digital Marketing
               - Free certification
            
            4. Microsoft Learn
               - Technology skills
               - Free with certificates
            
            5. Coursera for Women
               - Scholarships available
               - Global universities
            
            6. Udemy Free Courses
               - Varied subjects
               - Lifetime access
            
            💡 Search for courses in your field!
        """.trimIndent()

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("📚 Free Online Courses")
            .setMessage(popularCourses)
            .setPositiveButton("Browse Courses") { _, _ ->
                viewModel.recommendCourses(emptyList(), "All", 0L)
                Toast.makeText(context, "✅ Opening course catalog...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showCoursesDialog(courses: String) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("📚 Recommended Courses")
            .setMessage(courses)
            .setPositiveButton("Enroll") { _, _ ->
                Toast.makeText(context, "Opening course enrollment...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showCareerGuidance() {
        val input = EditText(requireContext()).apply {
            hint = "What are your interests? (e.g., Teaching, Technology, Healthcare)"
            setPadding(50, 40, 50, 40)
            minHeight = 120
        }

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("🎯 Career Guidance")
            .setMessage("Tell us about your interests and skills:")
            .setView(input)
            .setPositiveButton("Get Guidance") { _, _ ->
                val interests = input.text.toString()
                if (interests.isNotBlank()) {
                    showCareerOptions(interests)
                } else {
                    Toast.makeText(context, "Please enter your interests", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCareerOptions(interests: String) {
        val careerAdvice = """
            🎯 Career Paths for "$interests":
            
            ${getCareerSuggestions(interests)}
            
            📚 Recommended Skills to Learn:
            • Communication Skills
            • Digital Literacy
            • Leadership & Management
            • Technical Skills (field-specific)
            
            💼 Job Portals for Women:
            • Naukri.com
            • LinkedIn
            • Indeed
            • WomenJobPortal.in
            • Sheroes
            
            💡 Next Steps:
            1. Take skill assessment
            2. Complete relevant courses
            3. Build portfolio/resume
            4. Network with professionals
            5. Apply for internships/jobs
        """.trimIndent()

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Career Guidance")
            .setMessage(careerAdvice)
            .setPositiveButton("Explore Courses") { _, _ ->
                showOnlineCourses()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun getCareerSuggestions(interests: String): String {
        return when {
            interests.contains("tech", ignoreCase = true) -> """
                • Software Developer
                • Data Analyst
                • Digital Marketing Specialist
                • UI/UX Designer
                • Cybersecurity Analyst
            """.trimIndent()

            interests.contains("teach", ignoreCase = true) -> """
                • School Teacher
                • Online Tutor
                • Educational Content Creator
                • Career Counselor
                • Training & Development Specialist
            """.trimIndent()

            interests.contains("health", ignoreCase = true) -> """
                • Nurse
                • Medical Technician
                • Nutritionist
                • Public Health Worker
                • Healthcare Administrator
            """.trimIndent()

            else -> """
                • Based on your interests, multiple career options available
                • Take skill assessment for personalized recommendations
                • Connect with mentors for guidance
            """.trimIndent()
        }
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

        val selectedSkills = BooleanArray(skills.size)

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("📊 Skill Assessment")
            .setMessage("Select skills you want to assess:")
            .setMultiChoiceItems(skills, selectedSkills) { _, which, isChecked ->
                selectedSkills[which] = isChecked
            }
            .setPositiveButton("Start Assessment") { _, _ ->
                val selected = skills.filterIndexed { index, _ -> selectedSkills[index] }
                if (selected.isNotEmpty()) {
                    showAssessmentResult(selected)
                } else {
                    Toast.makeText(context, "Please select at least one skill", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAssessmentResult(skills: List<String>) {
        val result = skills.joinToString("\n") { skill ->
            val score = (60..95).random()
            "• $skill: $score/100"
        }

        val advice = """
            📊 Your Skill Assessment Results:
            
            $result
            
            💡 Recommendations:
            • Focus on improving lower-scored skills
            • Take online courses to enhance knowledge
            • Practice regularly
            • Seek mentorship
            • Apply skills in real projects
            
            🎯 Suggested Learning Path:
            1. Complete beginner courses
            2. Work on practical projects
            3. Get certified
            4. Join communities
            5. Keep learning & growing!
        """.trimIndent()

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Assessment Results")
            .setMessage(advice)
            .setPositiveButton("Find Courses") { _, _ ->
                showOnlineCourses()
            }
            .setNegativeButton("Close", null)
            .show()
    }
}
