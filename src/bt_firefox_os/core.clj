(ns bt-firefox-os.core
  (:import [com.braintreegateway BraintreeGateway Environment TransactionRequest])
  (:use [net.cgrand.enlive-html])
  (:require [noir.core :as noir]
            [cheshire.core :as json]
            [hiccup.core :as hiccup]
            [hiccup.page-helpers :as page-helper]
            [noir.response :as response]
            [noir.request :as noir-req]
            [noir.server :as server]))

(def gateway (BraintreeGateway.
              (System/getenv "BRAINTREE_ENV")
              (System/getenv "BRAINTREE_MERCHANT_ID")
              (System/getenv "BRAINTREE_PUBLIC_KEY")
              (System/getenv "BRAINTREE_PRIVATE_KEY")))

(defn build-transaction-request [cc-num amount exp-date cvv]
  (-> (TransactionRequest.)
      (.amount amount)
      (.creditCard)
      (.number cc-num)
      (.expirationDate exp-date)
      (.cvv cvv)
      (.done)))

(defn create-transaction [cc-num amount exp-date cvv]
  (let [transaction (build-transaction-request cc-num amount exp-date cvv)
        result (-> gateway (.transaction)
                   (.sale transaction))]
    result))

(def cse-key (System/getenv "BRAINTREE_CSE_KEY"))

(deftemplate form-page "home.html" []
  [:ul.errors] nil
  [:input#cse-key] (set-attr :value cse-key))

(deftemplate success-page "success.html" [])

(defn render-success [] (success-page))

(deftemplate render-fail "home.html" [result]
  [:title] (content "Donation Failed")
  [:li.error] (clone-for [error (.getAllDeepValidationErrors (.getErrors result))]
                         (content (format "%s: %s" (.getAttribute error) (.getMessage error)))))

(noir/defpage "/" []
  (form-page))

(noir/defpage "/manifest" []
  (response/content-type
   "application/x-web-app-manifest+json"
   (json/generate-string  {:name "Braintree Example"
                           :description "An Example for Using Braintree on Firefox OS"
                           :launch_path "/"
                           :icons {420 "/images/braintree.png"}
                           :developer { :name "Your Name Here"
                                       :url "YOUR URL HERE"}
                           :default_locale "en" })))

(noir/defpage [:post "/"] {:keys [ cc-num amount exp-date cvv phone-num]}
  (let [result (create-transaction cc-num (BigDecimal. amount) exp-date cvv)]
    (if (.isSuccess result)
      (render-success)
      (render-fail result))))

(defn start [port]
  (server/start port))

(defn- main []
  (server/start (System/getenv "PORT")))
