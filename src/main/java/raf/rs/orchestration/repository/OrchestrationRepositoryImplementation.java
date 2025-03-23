package raf.rs.orchestration.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import raf.rs.orchestration.model.TextChannelWithPermission;

import java.util.List;
@AllArgsConstructor
@Repository
public class OrchestrationRepositoryImplementation implements OrchestrationRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<TextChannelWithPermission> findTextChannelsWithParentsAndPermissions(Long userId){

        String sql = "SELECT \n" +
                "    tc.id AS text_channel_id, \n" +
                "    tc.name AS text_channel_name, \n" +
                "    tc.description AS text_channel_description, \n" +
                "    vc.id AS voice_channel_id, \n" +
                "    vc.name AS voice_channel_name, \n" +
                "    vc.description AS voice_channel_description, \n" +
                "    s.id AS studies_id, \n" +
                "    s.name AS studies_name, \n" +
                "    s.description AS studies_description, \n" +
                "    sp.id AS study_program_id, \n" +
                "    sp.name AS study_program_name, \n" +
                "    sp.description AS study_program_description, \n" +
                "    c.id AS category_id, \n" +
                "    c.name AS category_name, \n" +
                "    c.description AS category_description, \n" +
                "\n" +
                "    -- Calculate text channel write permission\n" +
                "    MAX(CASE WHEN MOD(tcr.permissions / 2, 2) = 1 THEN 1 ELSE 0 END) AS text_has_write_permission,\n" +
                "    \n" +
                "    -- Calculate voice channel permission (similar logic as text channels)\n" +
                "    MAX(CASE WHEN MOD(vcr.permissions / 2, 2) = 1 THEN 1 ELSE 0 END) AS voice_has_speak_permission \n" +
                "\n" +
                "FROM STUDIES s \n" +
                "JOIN STUDIES_STUDY_PROGRAMS ssp ON s.id = ssp.studies_id \n" +
                "JOIN STUDY_PROGRAM sp ON ssp.study_programs_id = sp.id \n" +
                "JOIN STUDY_PROGRAM_SUBJECT sps ON sp.id = sps.study_program_id \n" +
                "JOIN CATEGORY c ON sps.category_id = c.id \n" +
                "\n" +
                "-- TEXT CHANNEL JOINS\n" +
                "LEFT JOIN CATEGORY_TEXT_CHANNELS ctc ON c.id = ctc.category_id \n" +
                "LEFT JOIN TEXT_CHANNEL tc ON ctc.text_channels_id = tc.id \n" +
                "LEFT JOIN TEXT_CHANNEL_ROLE tcr ON tc.id = tcr.text_channel_id \n" +
                "\n" +
                "-- VOICE CHANNEL JOINS\n" +
                "LEFT JOIN CATEGORY_VOICE_CHANNELS cvc ON c.id = cvc.category_id \n" +
                "LEFT JOIN VOICE_CHANNEL vc ON cvc.voice_channels_id = vc.id\n" +
                "LEFT JOIN VOICE_CHANNEL_ROLE vcr ON vc.id = vcr.voice_channel_id \n" +
                "\n" +
                "-- USER ROLE VALIDATION\n" +
                "JOIN \"ROLE\" r ON (tcr.role_id = r.id OR vcr.role_id = r.id)\n" +
                "JOIN USER_ROLES ur ON r.id = ur.role_id \n" +
                "JOIN MY_USER u ON ur.user_id = u.id \n" +
                "\n" +
                "WHERE u.id = ? \n" +
                "AND ( \n" +
                "    EXISTS (SELECT 1 FROM USER_ROLES ur_admin \n" +
                "            JOIN \"ROLE\" r_admin ON ur_admin.role_id = r_admin.id \n" +
                "            WHERE ur_admin.user_id = u.id AND r_admin.name = 'ADMIN') \n" +
                "\n" +
                "    OR tc.id IN ( \n" +
                "        SELECT DISTINCT tc.id \n" +
                "        FROM STUDIES s \n" +
                "        JOIN STUDIES_STUDY_PROGRAMS ssp ON s.id = ssp.studies_id \n" +
                "        JOIN STUDY_PROGRAM sp ON ssp.study_programs_id = sp.id \n" +
                "        JOIN STUDY_PROGRAM_SUBJECT sps ON sp.id = sps.study_program_id \n" +
                "        JOIN CATEGORY c ON sps.category_id = c.id \n" +
                "        JOIN CATEGORY_TEXT_CHANNELS ctc ON c.id = ctc.category_id \n" +
                "        JOIN TEXT_CHANNEL tc ON ctc.text_channels_id = tc.id \n" +
                "        JOIN TEXT_CHANNEL_ROLE tcr ON tc.id = tcr.text_channel_id \n" +
                "        JOIN \"ROLE\" r ON tcr.role_id = r.id \n" +
                "        JOIN USER_ROLES ur ON r.id = ur.role_id \n" +
                "        JOIN MY_USER u ON ur.user_id = u.id \n" +
                "        WHERE u.id = ? \n" +
                "        AND r.name != UPPER(r.name) \n" +
                "    )\n" +
                "\n" +
                "    OR vc.id IN ( \n" +
                "        SELECT DISTINCT vc.id \n" +
                "        FROM STUDIES s \n" +
                "        JOIN STUDIES_STUDY_PROGRAMS ssp ON s.id = ssp.studies_id \n" +
                "        JOIN STUDY_PROGRAM sp ON ssp.study_programs_id = sp.id \n" +
                "        JOIN STUDY_PROGRAM_SUBJECT sps ON sp.id = sps.study_program_id \n" +
                "        JOIN CATEGORY c ON sps.category_id = c.id \n" +
                "        JOIN CATEGORY_VOICE_CHANNELS cvc ON c.id = cvc.category_id \n" +
                "        JOIN VOICE_CHANNEL vc ON cvc.voice_channels_id = vc.id \n" +
                "        JOIN VOICE_CHANNEL_ROLE vcr ON vc.id = vcr.voice_channel_id \n" +
                "        JOIN \"ROLE\" r ON vcr.role_id = r.id \n" +
                "        JOIN USER_ROLES ur ON r.id = ur.role_id \n" +
                "        JOIN MY_USER u ON ur.user_id = u.id \n" +
                "        WHERE u.id = ? \n" +
                "        AND r.name != UPPER(r.name) \n" +
                "    )\n" +
                ") \n" +
                "\n" +
                "GROUP BY tc.id, tc.name, tc.description, \n" +
                "         vc.id, vc.name, vc.description, \n" +
                "         s.id, s.name, s.description, \n" +
                "         sp.id, sp.name, sp.description, \n" +
                "         c.id, c.name, c.description \n" +
                "\n" +
                "HAVING MAX(CASE WHEN r.name != UPPER(r.name) THEN tcr.permissions ELSE NULL END) IS NOT NULL \n" +
                "   OR MAX(CASE WHEN r.name != UPPER(r.name) THEN vcr.permissions ELSE NULL END) IS NOT NULL\n" +
                "   OR EXISTS (SELECT 1 FROM USER_ROLES ur_admin \n" +
                "              JOIN \"ROLE\" r_admin ON ur_admin.role_id = r_admin.id \n" +
                "              WHERE ur_admin.user_id = ? AND r_admin.name = 'ADMIN');\n";


        return jdbcTemplate.query(sql, new Object[]{userId, userId, userId, userId},
                new BeanPropertyRowMapper<>(TextChannelWithPermission.class));
    }
}
