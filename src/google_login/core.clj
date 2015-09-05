(ns google-login.core
  (:require [clj-http.client :as client]
            [clj-http.cookies :as cookies])
  (:gen-class))


(def cookie-store (cookies/cookie-store))

;;Enter to the Google login page to get session cookes
(defn get-login-page-with-cookies! [continue-url]
  (let [url "https://www.google.com/accounts/ServiceLogin"
        query-params {:service  "ah"
                      :passive  true
                      :continue continue-url
                      :ltmpl    "gm"
                      :shdf     "ChMLEgZhaG5hbWUaB0luZ3Jlc3MMEgJhaCIUDxXHTvPWkR39qgc9Ntp6RlMnsagoATIUG3HUffbxSU31LjICBdNoinuaikg"}]
    (client/get url {:query-params query-params
                     :cookie-store cookie-store})))

;;Post login form with cookies from page
(defn- post-login! [login password continue-url]
  (let [url "https://accounts.google.com/ServiceLoginAuth"
        cookies-map (cookies/get-cookies cookie-store)
        GALX-cookie (get-in cookies-map ["GALX" :value])
        form-params {"GALX"             GALX-cookie
                     "continue"         continue-url        ;; страница перехода
                     "service"          "ah"
                     "ltmpl"            "gm"
                     "checkedDomains"   "youtube"
                     "checkConnection"  "youtube:136:1"
                     "pstMsg"           "1"
                     "_utf8"            "&#9731"
                     "bgresponse"       "js_disabled"
                     "Email"            login
                     "Passwd"           password
                     "PersistentCookie" "yes"
                     "signIn"           "Sign in"
                     "dnConn"           ""}]
    (client/post url {:form-params  form-params
                      :cookie-store cookie-store})))
;;TODO: error checks
;;Perform login to Google account
(defn login! [email password continue-url]
  (get-login-page-with-cookies! continue-url)               ;;go to login page to get some cookies
  (let [login-resp (post-login! email password continue-url) ;;post login form with credentials
        redirect-url (get-in login-resp [:headers "Location"]) ;;get redirect url
        redirect-url-resp (client/get redirect-url {:cookie-store cookie-store})
        logged-in-resp (get-login-page-with-cookies! continue-url)] ;;go to login page to get content
    logged-in-resp))
