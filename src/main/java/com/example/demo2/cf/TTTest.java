package com.example.demo2.cf;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author wlei3
 * @since 2022/5/31 20:55
 */
public class TTTest {

    public void test() throws IOException {
        File jsonFile = ResourceUtils.getFile("classpath:111.json");
        String json = FileUtils.readFileToString(jsonFile);
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> maps = mapper.readValue(json,
            new TypeReference<List<Map<String, Object>>>() {
            });

        HashSet<Integer> result = Sets.newHashSet();
        HashSet<String> sets = Sets.newHashSet("rank_type", "rank_type__name", "professional_rank_type", "professional_rank_type__name", "direct_superior", "dotted_line_leader", "professional_rank", "professional_rank__name", "management_rank", "management_rank__name", "if_probation_period__name", "welfare_places", "welfare_places__name", "working_city__name", "direct_superior__info", "dotted_line_leader__info", "if_probation_period", "begin_date", "company_senority", "seniority", "ceshishuxing", "ceshishuxing__name", "jiguan", "if_correct", "if_correct__name", "new_progress", "new_progress__name", "leave_salary", "leave_salary__name", "month_transfer_salary", "month_transfer_salary__name", "month_salary_adjust", "month_salary_adjust__name", "basic_wage_standard", "rank_allowance_standard", "post_allowance_standard", "food_paste_standard", "trans_subsidy_standard", "house_subsidy_standard", "person_perfor_bonus_standard", "nightshift_subsidy_standard", "remark", "default_role_bids", "nature_of_employees", "nature_of_employees__name", "employee_category", "employee_category__name", "date_of_part_to_full", "date_of_part_person_release", "employee_nature", "employee_nature__name", "city_province", "welfare_city", "welfare_city__name", "anbanci_paiban", "anbanci_paiban__name", "change_city_data");
        for (Map<String, Object> map : maps) {
            String fieldCode = (String) map.get("fieldCode");
            if (sets.contains(fieldCode)) {
                result.add((Integer)map.get("id"));
            }
        }
        System.out.println(result);
    }

    public static void main(String[] args) throws IOException {
        TTTest ttTest = new TTTest();
        ttTest.test();
    }
}
