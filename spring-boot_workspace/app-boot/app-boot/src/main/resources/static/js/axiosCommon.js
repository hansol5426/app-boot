// axios 인터셉터

axios.defaults.withCredentials = true; // 서버통신 오류 방지(cors)
axios.defaults.timeout = 10000; // 대기시간 1분

// 요청 가로채기
axios.interceptors.request.use(
  (config) => {
    console.log(`[Request] ${config.method?.toUpperCase()}, ${config.url}`);
    return config;
  },
  (error) => {
    console.log("[Request Error]", error);
    return Promise.reject(error);
  }
);

// 응답 가로채기
axios.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    errorHandler(error);
    // 인터셉터에서 처리 후 promise 리턴해야 axios 호출 위치에서 처리 가능
    return Promise.reject(error);
  }
);

function errorHandler(error) {
  if (!error.response) {
    alert("네트워크 상태를 확인하십시오.");
    return false;
  }

  const status = error.response.status;
  const data = error.response.data;

  switch (status) {
    case 400:
      alert("잘못된 요청입니다.");
      break;
    case 401:
      alert("로그인이 필요한 기능입니다.");
      break;
    case 403:
      alert("권한이 없습니다.");
      break;
    case 500:
      alert("서버 내부에 오류가 발생하였습니다.");
      break;
    default:
      alert("예상치 못한 오류가 발생하였습니다.");
  }
}
