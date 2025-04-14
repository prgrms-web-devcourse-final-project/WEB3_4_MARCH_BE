import type { KeywordCategoryResponse } from "../../api/__generated__";

export const DUMMY_KEYWORDS: KeywordCategoryResponse[] = [
  {
    categoryId: 1,
    categoryName: "장르",
    multipleChoice: true,
    keywords: [
      { id: 1, name: "액션" },
      { id: 2, name: "스릴러" },
      { id: 3, name: "로맨스" },
      { id: 4, name: "코미디" },
      { id: 5, name: "공포" },
      { id: 6, name: "SF" },
      { id: 7, name: "판타지" },
      { id: 8, name: "드라마" },
    ],
  },
  {
    categoryId: 2,
    categoryName: "시대",
    multipleChoice: false,
    keywords: [
      { id: 9, name: "현대" },
      { id: 10, name: "과거" },
      { id: 11, name: "미래" },
      { id: 12, name: "중세" },
      { id: 13, name: "고대" },
    ],
  },
  {
    categoryId: 3,
    categoryName: "분위기",
    multipleChoice: true,
    keywords: [
      { id: 14, name: "밝은" },
      { id: 15, name: "어두운" },
      { id: 16, name: "감성적인" },
      { id: 17, name: "유쾌한" },
      { id: 18, name: "긴장감 있는" },
      { id: 19, name: "신비로운" },
    ],
  },
  {
    categoryId: 4,
    categoryName: "주제",
    multipleChoice: true,
    keywords: [
      { id: 20, name: "사랑" },
      { id: 21, name: "우정" },
      { id: 22, name: "성장" },
      { id: 23, name: "복수" },
      { id: 24, name: "모험" },
      { id: 25, name: "생존" },
      { id: 26, name: "가족" },
    ],
  },
  {
    categoryId: 5,
    categoryName: "대상 연령",
    multipleChoice: false,
    keywords: [
      { id: 27, name: "전체 관람가" },
      { id: 28, name: "12세 이상" },
      { id: 29, name: "15세 이상" },
      { id: 30, name: "성인" },
    ],
  },
];
