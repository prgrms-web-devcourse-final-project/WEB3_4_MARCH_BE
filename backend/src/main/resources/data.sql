# # 키워드 데이터
#
# # 1. 취미 & 관심사 (중복 가능)
#  INSERT INTO keyword_category (id, category_name, multiple_choice) VALUES (1, '취미 & 관심사', true);
#
#  INSERT INTO keyword (name, category_id) VALUES
#                                              ('등산', 1),
#                                             ('헬스', 1),
#                                              ('복싱', 1),
#                                              ('그림', 1),
#                                              ('기타 연주', 1),
#                                              ('독서', 1),
#                                              ('게임', 1),
#                                              ('요리', 1),
#                                              ('노래방', 1),
#                                              ('여행', 1),
#                                              ('사진', 1),
#                                              ('캠핑', 1),
#                                              ('축구', 1),
#                                              ('농구', 1),
#                                              ('요가', 1),
#                                             ('자전거', 1),
#                                              ('뮤지컬/연극', 1);
#
# # 2. 음주 (중복 불가능)
#  INSERT INTO keyword_category (id, category_name, multiple_choice) VALUES (2, '음주', false);
#
#  INSERT INTO keyword (name, category_id) VALUES
#                                              ('술 안 먹어요', 2),
#                                              ('가끔 한 잔', 2),
#                                              ('1주에 한 번', 2),
#                                              ('분위기 타면 마셔요', 2),
#                                              ('주량은 비밀!', 2);
#
# # 3. 흡연 (중복 불가능)
#  INSERT INTO keyword_category (id, category_name, multiple_choice) VALUES (3, '흡연', false);
#
#  INSERT INTO keyword (name, category_id) VALUES
#                                              ('비흡연자', 3),
#                                              ('가끔', 3),
#                                              ('흡연자', 3);
#
# #  4. 연애관 & 연애 스타일 (중복 가능)
# INSERT INTO keyword_category (id, category_name, multiple_choice) VALUES (4, '연애관 & 연애 스타일', true);
#
# INSERT INTO keyword (name, category_id) VALUES
#                                              ('지금 당장 연애하고 싶어요', 4),
#                                              ('천천히 알아가고 싶어요', 4),
#                                              ('친구도 연애도 좋아요', 4),
#                                              ('친구를 만나고 싶어요', 4);
#
# #  5. 성격 (중복 가능)
#  INSERT INTO keyword_category (id, category_name, multiple_choice) VALUES (5, '성격', true);
#  INSERT INTO keyword (name, category_id) VALUES
#                                              ('활발한 편', 5),
#                                              ('수줍음 많아요', 5),
#                                              ('차분한 편', 5),
#                                              ('긍정적이에요', 5),
#                                              ('신중한 스타일', 5),
#                                              ('외향적', 5),
#                                              ('내향적', 5);
#
# # 6. 가치관 & 라이프스타일 (중복 가능)
#  INSERT INTO keyword_category (id, category_name, multiple_choice) VALUES (6, '가치관 & 라이프스타일', true);
#
#  INSERT INTO keyword (name, category_id) VALUES
#                                              ('일과 삶의 균형 중요해요', 6),
#                                              ('집순이 / 집돌이', 6),
#                                              ('바깥 활동 좋아해요', 6),
#                                              ('소비보단 저축', 6),
#                                              ('소중한 사람들에게 잘 베풀어요', 6),
#                                              ('목표 지향적인 성격', 6);
#
# # 7. 이상형 (중복 가능)
#  INSERT INTO keyword_category (id, category_name, multiple_choice) VALUES (7, '이상형', true);
#
#  INSERT INTO keyword (name, category_id) VALUES
#                                              ('유머 코드 잘 맞는 사람', 7),
#                                              ('취미가 비슷한 사람', 7),
#                                              ('가치관이 비슷한 사람', 7),
#                                              ('대화가 잘 통하는 사람', 7),
#                                              ('서로 배려하는 사람', 7);