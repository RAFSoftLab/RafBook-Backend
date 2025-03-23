package raf.rs.seeder;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import raf.rs.messagingservice.model.*;
import raf.rs.messagingservice.repository.*;
import raf.rs.userservice.model.MyUser;
import raf.rs.userservice.model.Role;
import raf.rs.userservice.repository.RoleRepository;
import raf.rs.userservice.repository.UserRepository;
import raf.rs.voiceservice.model.VoiceChannel;
import raf.rs.voiceservice.model.VoiceChannelRole;
import raf.rs.voiceservice.repository.VoiceChannelRepository;
import raf.rs.voiceservice.repository.VoiceChannelRoleRepository;
import raf.rs.voiceservice.service.VoiceChannelService;

import java.util.*;

@Component
public class Seeder implements CommandLineRunner {
    private CategoryRepository categoryRepository;
    private MessageRepository messageRepository;
    private StudiesRepository studiesRepository;
    private StudyProgramRepository studyProgramRepository;
    private TextChannelRepository textChannelRepository;
    private TextChannelRoleRepository textChannelRoleRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private VoiceChannelRoleRepository voiceChannelRoleRepository;
    private VoiceChannelRepository voiceChannelRepository;

    public Seeder(CategoryRepository categoryRepository, MessageRepository messageRepository
            , StudiesRepository studiesRepository, StudyProgramRepository studyProgramRepository
            , TextChannelRepository textChannelRepository, TextChannelRoleRepository textChannelRoleRepository
            , UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder
            , VoiceChannelRoleRepository voiceChannelRoleRepository, VoiceChannelRepository voiceChannelRepository) {
        this.categoryRepository = categoryRepository;
        this.messageRepository = messageRepository;
        this.studiesRepository = studiesRepository;
        this.studyProgramRepository = studyProgramRepository;
        this.textChannelRepository = textChannelRepository;
        this.textChannelRoleRepository = textChannelRoleRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.voiceChannelRoleRepository = voiceChannelRoleRepository;
        this.voiceChannelRepository = voiceChannelRepository;
    }
    private HashMap<String ,Role> roles = new HashMap<>();
    private MyUser mara;



    @Override
    public void run(String... args) throws Exception {
        seed();
    }

    private void seed() {
        createRoles();
        createUsers();
        createTheWorld();
    }

    private Role createRole(String name) {
        if(roles.containsKey(name)) {
            return roles.get(name);
        }
        Role role = new Role();
        role.setName(name);
        role = roleRepository.save(role);
        roles.put(name, role);
        return role;
    }

    private MyUser createUsers(String firstName, String lastName, String username, String email, String password, ArrayList<Role> roles) {
        // Create admin
        MyUser user = new MyUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setHashPassword(passwordEncoder.encode(password));
        user.setRoles(new HashSet<>(roles));
        return userRepository.save(user);
    }

    private void createRoles() {
        List<String> roleNames = Arrays.asList(
                "ADMIN", "STUDENT", "PROFESSOR", "TEACHING_ASSISTANT", "TEACHING_ASSOCIATE",
                "Linearna algebra i analiticka geometrija", "Diskretne strukture", "Uvod u programiranje",
                "Engleski 1", "Poslovne aplikacije", "Matematicka analiza", "Osnovi racunarskih sistema",
                "Objektno-orijentisano programiranje", "Engleski 2", "Pismeno i usmeno izrazavanje",
                "Algoritmi i strukture podataka", "Dizajn softvera", "Inteligentni sistemi",
                "Napredna matematicka analiza", "Operativni sistemi", "Racunarske mreze",
                "Baze podataka", "Dizajn i analiza algoritama", "Verovatnoca i statistika",
                "Programski prevodioci", "Paralelni algoritmi", "Testiranje softvera",
                "Osnovi nauke o podacima", "Uvod u pronalazenje informacija",
                "Uvod u robotiku", "Racunarska grafika", "Geometrijski algoritmi",
                "Softverske komponente", "Skript jezici", "Interakcija covek-racunar", "Algebra",
                "Uvod u bioinformatiku", "Masinsko ucenje", "Analitika i vizualizacija podataka",
                "Genetski algoritmi", "Neuronse mreze", "Kriptografija", "Funkcionalno programiranje",
                "Web programiranje", "Integrisani informacioni sistemi", "Razvoj mobilnih aplikacija",
                "Kombinatorika i teorija grafova", "Upravljanje projektima", "Veliki podaci",
                "Duboko ucenje", "Skladista podataka", "Prepoznavanje govora", "Kompjuterski vid",
                "Modelovanje i simulacija", "Kvantno racunarstvo", "Multimedijalni informacioni sistemi",
                "Napredno web programiranje", "Teorija algoritama, automata i jezika",
                "Softversko inzenjerstvo", "Konkurentni i distribuirani sistemi"
        );
        for (String roleName : roleNames) {
            createRole(roleName);
        }
    }

    private void createUsers() {
        mara = createUsers("Mara", "Arbutina", "mara", "mara@raf.rs"
                , "mara123", new ArrayList<>(Arrays.asList(roles.get("ADMIN"))));
        createUsers("Stevan", "Dabizljevic", "stevan", "sdabizljevic@raf.rs"
                , "steva123", new ArrayList<>(Arrays.asList(roles.get("STUDENT")
                        , roles.get("TEACHING_ASSOCIATE")
                        , roles.get("Softverske komponente")
                        , roles.get("Skript jezici")
                        , roles.get("Interakcija covek-racunar")
                        , roles.get("Algebra")
                        , roles.get("Uvod u bioinformatiku")
                )));
        createUsers("Milan", "Marinkovic", "marinzv", "mmarinkovic7324m@raf.rs"
                , "marin123", new ArrayList<>(Arrays.asList(roles.get("STUDENT")
                        , roles.get("Softverske komponente")
                        , roles.get("Skript jezici")
                        , roles.get("Interakcija covek-racunar")
                        , roles.get("Skladista podataka")
                        , roles.get("Uvod u bioinformatiku")
                        , roles.get("Verovatnoca i statistika")
                        , roles.get("Algebra")
                )));
        createUsers("Mitar", "Kajganic", "mitar", "mkajganic@raf.rs"
                , "mitar123", new ArrayList<>(Arrays.asList(roles.get("STUDENT")
                        , roles.get("Softverske komponente")
                        , roles.get("Skript jezici")
                        , roles.get("Interakcija covek-racunar")
                )));
        createUsers("Petar", "Stamenic", "petar", "pstamenic1524m@raf.rs"
                , "marin123", new ArrayList<>(Arrays.asList(roles.get("STUDENT")
                        , roles.get("Softverske komponente")
                )));
    }

    private void createTheWorld(){
        List<String> categories = Arrays.asList(
                "General",
                "Linearna algebra i analiticka geometrija", "Diskretne strukture", "Uvod u programiranje",
                "Engleski 1", "Poslovne aplikacije", "Matematicka analiza", "Osnovi racunarskih sistema",
                "Objektno-orijentisano programiranje", "Engleski 2", "Pismeno i usmeno izrazavanje",
                "Algoritmi i strukture podataka", "Dizajn softvera", "Inteligentni sistemi",
                "Napredna matematicka analiza", "Operativni sistemi", "Racunarske mreze",
                "Baze podataka", "Dizajn i analiza algoritama", "Verovatnoca i statistika",
                "Programski prevodioci", "Paralelni algoritmi", "Testiranje softvera",
                "Osnovi nauke o podacima", "Uvod u pronalazenje informacija",
                "Uvod u robotiku", "Racunarska grafika", "Geometrijski algoritmi",
                "Softverske komponente", "Skript jezici", "Interakcija covek-racunar", "Algebra",
                "Uvod u bioinformatiku", "Masinsko ucenje", "Analitika i vizualizacija podataka",
                "Genetski algoritmi", "Neuronse mreze", "Kriptografija", "Funkcionalno programiranje",
                "Web programiranje", "Integrisani informacioni sistemi", "Razvoj mobilnih aplikacija",
                "Kombinatorika i teorija grafova", "Upravljanje projektima", "Veliki podaci",
                "Duboko ucenje", "Skladista podataka", "Prepoznavanje govora", "Kompjuterski vid",
                "Modelovanje i simulacija", "Kvantno racunarstvo", "Multimedijalni informacioni sistemi",
                "Napredno web programiranje", "Teorija algoritama, automata i jezika",
                "Softversko inzenjerstvo", "Konkurentni i distribuirani sistemi"
        );

        List<String> studyPrograms = Arrays.asList(
                "Racunarske nauke"
        );

        Studies studies = new Studies();
        studies.setName("Osnovne akademske studije");
        studies.setDescription("Osnovne akademske studije na Racunarskom fakultetu");

        studies.setStudyPrograms(createStudyPrograms(studyPrograms, categories));
        studiesRepository.save(studies);
    }

    private Set<StudyProgram> createStudyPrograms(List<String> studyPrograms , List<String> categories) {
        HashSet<StudyProgram> studyProgramSet = new HashSet<>();
        for (String studyProgramStr : studyPrograms) {
            StudyProgram studyProgram = new StudyProgram();
            studyProgram.setName(studyProgramStr);
            studyProgram.setDescription(studyProgramStr + " na Racunarskom fakultetu");
            studyProgram.setCategories(createCategories(categories));
            studyProgramSet.add(studyProgramRepository.save(studyProgram));
        }
        return studyProgramSet;
    }

    private Set<Category> createCategories(List<String> categories) {
        Set<Category> categorySet = new HashSet<>();
        for (String categoryStr : categories) {
            Category category = new Category();
            category.setName(categoryStr);
            category.setDescription(categoryStr + " na Racunarskom fakultetu");
            category.setTextChannels(createTextChannels(categoryStr));
            category.setVoiceChannels(Set.of(createVoiceChannel(categoryStr)));
            categorySet.add(categoryRepository.save(category));
        }
        return categorySet;
    }

    private Set<TextChannel> createTextChannels(String categoryStr) {
        Set<TextChannel> textChannelSet = new HashSet<>();

        TextChannel general = new TextChannel();
        general.setName("General");
        general.setDescription("General channel for " + categoryStr);
        general.setMessages(new HashSet<>());
        general = textChannelRepository.save(general);
        textChannelSet.add(general);

        Message message = new Message();
        message.setContent("Welcome to " + categoryStr + " channel!");
        message.setSender(mara);
        message.setType(MessageType.TEXT);
        message.setTextChannel(general);
        messageRepository.save(message);

        if(categoryStr.equals("General")) {
            for(Role role: roles.values()) {
                TextChannelRole textChannelRole = new TextChannelRole();
                textChannelRole.setTextChannel(general);
                textChannelRole.setRole(role);
                textChannelRole.setPermissions(3L);
                textChannelRoleRepository.save(textChannelRole);
            }
        } else {
            TextChannelRole admin = new TextChannelRole();
            admin.setTextChannel(general);
            admin.setRole(roles.get("ADMIN"));
            admin.setPermissions(3L);
            textChannelRoleRepository.save(admin);

            TextChannelRole rol = new TextChannelRole();
            rol.setTextChannel(general);
            rol.setRole(roles.get(categoryStr));
            rol.setPermissions(3L);
            textChannelRoleRepository.save(rol);

            TextChannelRole student = new TextChannelRole();
            student.setTextChannel(general);
            student.setRole(roles.get("STUDENT"));
            student.setPermissions(3L);
            textChannelRoleRepository.save(student);

            TextChannelRole professor = new TextChannelRole();
            professor.setTextChannel(general);
            professor.setRole(roles.get("PROFESSOR"));
            professor.setPermissions(3L);
            textChannelRoleRepository.save(professor);

            TextChannelRole teachingAssistant = new TextChannelRole();
            teachingAssistant.setTextChannel(general);
            teachingAssistant.setRole(roles.get("TEACHING_ASSISTANT"));
            teachingAssistant.setPermissions(3L);
            textChannelRoleRepository.save(teachingAssistant);

            TextChannelRole teachingAssociate = new TextChannelRole();
            teachingAssociate.setTextChannel(general);
            teachingAssociate.setRole(roles.get("TEACHING_ASSOCIATE"));
            teachingAssociate.setPermissions(3L);
            textChannelRoleRepository.save(teachingAssociate);
        }

        TextChannel propaganda = new TextChannel();
        propaganda.setName("Propaganda");
        propaganda.setDescription("Propaganda channel for " + categoryStr);
        propaganda.setMessages(new HashSet<>());
        propaganda = textChannelRepository.save(propaganda);
        textChannelSet.add(propaganda);


        Message messageP = new Message();
        messageP.setContent("Welcome to " + categoryStr + " channel!");
        messageP.setSender(mara);
        messageP.setType(MessageType.TEXT);
        messageP.setTextChannel(propaganda);
        messageRepository.save(messageP);

        if(categoryStr.equals("General")) {
            for(Role role: roles.values()) {
                TextChannelRole textChannelRole = new TextChannelRole();
                textChannelRole.setTextChannel(propaganda);
                textChannelRole.setRole(role);
                textChannelRole.setPermissions(3L);
                textChannelRoleRepository.save(textChannelRole);
            }
        } else {
            TextChannelRole admin = new TextChannelRole();
            admin.setTextChannel(propaganda);
            admin.setRole(roles.get("ADMIN"));
            admin.setPermissions(3L);
            textChannelRoleRepository.save(admin);

            TextChannelRole rol = new TextChannelRole();
            rol.setTextChannel(propaganda);
            rol.setRole(roles.get(categoryStr));
            rol.setPermissions(1L);
            textChannelRoleRepository.save(rol);

            TextChannelRole student = new TextChannelRole();
            student.setTextChannel(propaganda);
            student.setRole(roles.get("STUDENT"));
            student.setPermissions(1L);
            textChannelRoleRepository.save(student);

            TextChannelRole professor = new TextChannelRole();
            professor.setTextChannel(propaganda);
            professor.setRole(roles.get("PROFESSOR"));
            professor.setPermissions(3L);
            textChannelRoleRepository.save(professor);

            TextChannelRole teachingAssistant = new TextChannelRole();
            teachingAssistant.setTextChannel(propaganda);
            teachingAssistant.setRole(roles.get("TEACHING_ASSISTANT"));
            teachingAssistant.setPermissions(3L);
            textChannelRoleRepository.save(teachingAssistant);

            TextChannelRole teachingAssociate = new TextChannelRole();
            teachingAssociate.setTextChannel(propaganda);
            teachingAssociate.setRole(roles.get("TEACHING_ASSOCIATE"));
            teachingAssociate.setPermissions(3L);
            textChannelRoleRepository.save(teachingAssociate);
        }

        TextChannel archive = new TextChannel();
        archive.setName("Archive");
        archive.setDescription("Archive channel for " + categoryStr);
        archive.setMessages(new HashSet<>());
        archive = textChannelRepository.save(archive);
        textChannelSet.add(archive);


        Message messageA = new Message();
        messageA.setContent("Welcome to " + categoryStr + " channel!");
        messageA.setSender(mara);
        messageA.setType(MessageType.TEXT);
        messageA.setTextChannel(archive);
        messageRepository.save(messageA);

        if(categoryStr.equals("General")) {
            for(Role role: roles.values()) {
                TextChannelRole textChannelRole = new TextChannelRole();
                textChannelRole.setTextChannel(archive);
                textChannelRole.setRole(role);
                textChannelRole.setPermissions(3L);
                textChannelRoleRepository.save(textChannelRole);
            }
        } else {
            TextChannelRole admin = new TextChannelRole();
            admin.setTextChannel(archive);
            admin.setRole(roles.get("ADMIN"));
            admin.setPermissions(3L);
            textChannelRoleRepository.save(admin);

            TextChannelRole rol = new TextChannelRole();
            rol.setTextChannel(archive);
            rol.setRole(roles.get(categoryStr));
            rol.setPermissions(3L);
            textChannelRoleRepository.save(rol);

            TextChannelRole student = new TextChannelRole();
            student.setTextChannel(archive);
            student.setRole(roles.get("STUDENT"));
            student.setPermissions(3L);
            textChannelRoleRepository.save(student);

            TextChannelRole professor = new TextChannelRole();
            professor.setTextChannel(archive);
            professor.setRole(roles.get("PROFESSOR"));
            professor.setPermissions(3L);
            textChannelRoleRepository.save(professor);

            TextChannelRole teachingAssistant = new TextChannelRole();
            teachingAssistant.setTextChannel(archive);
            teachingAssistant.setRole(roles.get("TEACHING_ASSISTANT"));
            teachingAssistant.setPermissions(3L);
            textChannelRoleRepository.save(teachingAssistant);

            TextChannelRole teachingAssociate = new TextChannelRole();
            teachingAssociate.setTextChannel(archive);
            teachingAssociate.setRole(roles.get("TEACHING_ASSOCIATE"));
            teachingAssociate.setPermissions(3L);
            textChannelRoleRepository.save(teachingAssociate);
        }

        return textChannelSet;
    }

    private VoiceChannel createVoiceChannel(String category) {
        VoiceChannel voiceChannel = new VoiceChannel();
        voiceChannel.setName("GeneralVoice");
        voiceChannel.setDescription("What do you think this is for?");
        voiceChannelRepository.save(voiceChannel);

        if(category.equals("General")) {
            for(Role role: roles.values()) {
                VoiceChannelRole voiceChannelRole = new VoiceChannelRole();
                voiceChannelRole.setVoiceChannel(voiceChannel);
                voiceChannelRole.setRole(role);
                voiceChannelRole.setPermissions(3L);
                voiceChannelRoleRepository.save(voiceChannelRole);
            }
        } else {

            VoiceChannelRole rol = new VoiceChannelRole();
            rol.setVoiceChannel(voiceChannel);
            rol.setRole(roles.get(category));
            rol.setPermissions(3L);
            voiceChannelRoleRepository.save(rol);

            VoiceChannelRole admin = new VoiceChannelRole();
            admin.setVoiceChannel(voiceChannel);
            admin.setRole(roles.get("ADMIN"));
            admin.setPermissions(3L);
            voiceChannelRoleRepository.save(admin);

            VoiceChannelRole student = new VoiceChannelRole();
            student.setVoiceChannel(voiceChannel);
            student.setRole(roles.get("STUDENT"));
            student.setPermissions(3L);
            voiceChannelRoleRepository.save(student);

            VoiceChannelRole professor = new VoiceChannelRole();
            professor.setVoiceChannel(voiceChannel);
            professor.setRole(roles.get("PROFESSOR"));
            professor.setPermissions(3L);
            voiceChannelRoleRepository.save(professor);

            VoiceChannelRole teachingAssistant = new VoiceChannelRole();
            teachingAssistant.setVoiceChannel(voiceChannel);
            teachingAssistant.setRole(roles.get("TEACHING_ASSISTANT"));
            teachingAssistant.setPermissions(3L);
            voiceChannelRoleRepository.save(teachingAssistant);
        }

        return voiceChannel;
    }
}
